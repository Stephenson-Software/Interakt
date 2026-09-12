package dansapps.interakt.tests;

import com.sun.net.httpserver.HttpServer;
import dansapps.interakt.Interakt;
import dansapps.interakt.services.LocalUsageReportingService;
import dansapps.interakt.utils.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Covers the usage reporting service: the first-run notice is printed exactly once, the settings
 * file is the opt-out, and the startup event that leaves the application carries only the program
 * name and version. Every test points at a temporary directory and, where a server is involved, a
 * local stub; nothing here talks to the real trace service.
 */
public class LocalUsageReportingServiceTest {
    private File settingsDirectory;
    private ByteArrayOutputStream console;
    private PrintStream out;
    private Logger logger;

    @Before
    public void setUp() throws IOException {
        settingsDirectory = Files.createTempDirectory("interakt-usage-reporting").toFile();
        console = new ByteArrayOutputStream();
        out = new PrintStream(console, true, "UTF-8");
        logger = new Logger(new Interakt());
    }

    @After
    public void tearDown() {
        File[] files = settingsDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        settingsDirectory.delete();
    }

    /**
     * Every service under test is built with an unroutable default endpoint, so a first run (which
     * has no settings file to read an endpoint from) can never reach the real trace service.
     */
    private LocalUsageReportingService newService() {
        return newService("http://127.0.0.1:9");
    }

    private LocalUsageReportingService newService(String defaultEndpoint) {
        return new LocalUsageReportingService(settingsDirectory, logger, out, defaultEndpoint);
    }

    /** A local stand-in for the trace service that records the one request it is sent. */
    private static final class StubTraceServer implements AutoCloseable {
        final AtomicReference<String> body = new AtomicReference<>();
        final AtomicReference<String> authorization = new AtomicReference<>();
        final CountDownLatch received = new CountDownLatch(1);
        private final HttpServer server;

        StubTraceServer() throws IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/api/metrics", exchange -> {
                try (InputStream in = exchange.getRequestBody()) {
                    body.set(new String(in.readAllBytes(), StandardCharsets.UTF_8));
                }
                authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
                exchange.sendResponseHeaders(201, -1);
                exchange.close();
                received.countDown();
            });
            server.start();
        }

        String endpoint() {
            return "http://127.0.0.1:" + server.getAddress().getPort();
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }

    private static String expectedStartupBody() {
        return "{\"application\":\"Interakt\",\"name\":\"startup\",\"tags\":{\"version\":\""
                + LocalUsageReportingService.getVersion() + "\"}}";
    }

    private void writeSettings(String contents) throws IOException {
        File file = new File(settingsDirectory, LocalUsageReportingService.SETTINGS_FILE_NAME);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(contents);
        }
    }

    @Test
    public void testFirstRunWritesDefaultsPrintsTheNoticeOnceAndReportsWithTheBundledKey() throws Exception {
        try (StubTraceServer stub = new StubTraceServer()) {
            LocalUsageReportingService service = newService(stub.endpoint());
            Assert.assertFalse(service.getSettingsFile().exists());

            service.start();
            Assert.assertTrue(service.isEnabled());
            Assert.assertTrue("no startup event arrived", stub.received.await(10, TimeUnit.SECONDS));
            service.close();

            Assert.assertEquals(expectedStartupBody(), stub.body.get());
            Assert.assertEquals("Bearer " + LocalUsageReportingService.DEFAULT_KEY, stub.authorization.get());

            String settings = new String(Files.readAllBytes(service.getSettingsFile().toPath()), StandardCharsets.UTF_8);
            Assert.assertTrue(settings.contains(LocalUsageReportingService.ENABLED_KEY + "=true\n"));
            Assert.assertTrue(settings.contains(LocalUsageReportingService.ENDPOINT_KEY + "=" + stub.endpoint() + "\n"));
            Assert.assertTrue(settings.contains(LocalUsageReportingService.KEY_KEY + "=" + LocalUsageReportingService.DEFAULT_KEY + "\n"));

            String printed = console.toString("UTF-8");
            Assert.assertEquals(service.getFirstRunNotice() + System.lineSeparator(), printed);
            Assert.assertTrue(printed, printed.startsWith("Usage reporting is on: Interakt sends a startup event (program name and version only) to trace.danielstephenson.dev. Turn it off with usage_reporting.enabled=false in "));

            // second run: the settings exist, so the notice is not printed again
            console.reset();
            LocalUsageReportingService secondRun = newService();
            secondRun.start();
            secondRun.close();
            Assert.assertEquals("", console.toString("UTF-8"));
        }
    }

    @Test
    public void testTheRealDefaultEndpointIsTheTraceService() {
        Assert.assertEquals("https://trace.danielstephenson.dev", LocalUsageReportingService.DEFAULT_ENDPOINT);
        Assert.assertEquals(LocalUsageReportingService.DEFAULT_ENDPOINT,
                new LocalUsageReportingService(settingsDirectory, logger, out).getDefaultEndpoint());
    }

    @Test
    public void testDisabledInSettingsSendsNothing() throws IOException {
        writeSettings(LocalUsageReportingService.ENABLED_KEY + "=false\n");

        LocalUsageReportingService service = newService();
        service.start();
        service.close();

        Assert.assertFalse(service.isEnabled());
        Assert.assertEquals("", console.toString("UTF-8"));
    }

    @Test
    public void testBlankKeySendsNothing() throws IOException {
        writeSettings(LocalUsageReportingService.ENABLED_KEY + "=true\n" + LocalUsageReportingService.KEY_KEY + "=\n");

        LocalUsageReportingService service = newService();
        service.start();
        service.close();

        Assert.assertFalse(service.isEnabled());
    }

    @Test
    public void testStartupEventReachesTheConfiguredEndpointWithNameAndVersionOnly() throws Exception {
        try (StubTraceServer stub = new StubTraceServer()) {
            writeSettings(LocalUsageReportingService.ENABLED_KEY + "=true\n"
                    + LocalUsageReportingService.ENDPOINT_KEY + "=" + stub.endpoint() + "\n"
                    + LocalUsageReportingService.KEY_KEY + "=test-key\n");

            LocalUsageReportingService service = newService();
            service.start();
            Assert.assertTrue(service.isEnabled());
            Assert.assertTrue("no startup event arrived", stub.received.await(10, TimeUnit.SECONDS));
            service.close();

            Assert.assertEquals(expectedStartupBody(), stub.body.get());
            Assert.assertEquals("Bearer test-key", stub.authorization.get());
            Assert.assertEquals("", console.toString("UTF-8"));
        }
    }

    @Test
    public void testVersionComesFromTheBuild() {
        String version = LocalUsageReportingService.getVersion();
        Assert.assertFalse("unknown".equals(version));
        Assert.assertFalse(version.contains("${"));
    }
}
