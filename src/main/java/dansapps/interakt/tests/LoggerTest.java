package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.objects.Event;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.utils.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Covers where the logger writes: the log file lives in the data directory, that directory is
 * created if it is missing, and the location is read when the logger is constructed rather than
 * once per JVM. Every test points the data directory at a temporary directory of its own.
 */
public class LoggerTest {
    private File temporaryDirectory;
    private String previousDataDirectory;

    @Before
    public void setUp() throws IOException {
        temporaryDirectory = Files.createTempDirectory("interakt-logger").toFile();
        previousDataDirectory = System.getProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY);
    }

    @After
    public void tearDown() {
        LocalStorageServiceTest.restoreDataDirectoryProperty(previousDataDirectory);
        delete(temporaryDirectory);
    }

    @Test
    public void testLogFileLivesInTheDataDirectory() {
        System.setProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY, temporaryDirectory.getPath());

        Logger logger = new Logger(new Interakt());

        Assert.assertEquals(new File(temporaryDirectory, Logger.LOG_FILE_NAME), logger.getFile());
        Assert.assertTrue(logger.getFile().getPath(), logger.getFile().isFile());
    }

    @Test
    public void testMissingDataDirectoryIsCreated() throws IOException {
        File missing = new File(temporaryDirectory, "not/yet/here");
        System.setProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY, missing.getPath());
        Assert.assertFalse(missing.exists());

        Logger logger = new Logger(new Interakt());
        logger.logEvent(new Event("event-sentinel"));

        Assert.assertTrue(missing.isDirectory());
        List<String> lines = Files.readAllLines(logger.getFile().toPath(), StandardCharsets.UTF_8);
        Assert.assertEquals(lines.toString(), 1, lines.size());
        Assert.assertTrue(lines.get(0), lines.get(0).endsWith("] event-sentinel"));
    }

    @Test
    public void testDataDirectoryIsResolvedWhenTheLoggerIsConstructed() {
        File first = new File(temporaryDirectory, "first");
        File second = new File(temporaryDirectory, "second");

        System.setProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY, first.getPath());
        Logger firstLogger = new Logger(new Interakt());
        System.setProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY, second.getPath());
        Logger secondLogger = new Logger(new Interakt());

        Assert.assertEquals(new File(first, Logger.LOG_FILE_NAME), firstLogger.getFile());
        Assert.assertEquals(new File(second, Logger.LOG_FILE_NAME), secondLogger.getFile());
    }

    private static void delete(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                delete(child);
            }
        }
        file.delete();
    }
}
