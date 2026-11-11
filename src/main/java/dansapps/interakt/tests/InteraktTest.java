package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.users.Console;
import dansapps.interakt.users.Player;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InteraktTest {

    @Test
    public void testConstructionStartsNoBackgroundServices() {
        Set<Thread> before = getLiveApplicationThreads();

        new Interakt();

        Set<Thread> after = getLiveApplicationThreads();
        after.removeAll(before);
        Assert.assertTrue("Constructing Interakt started background threads: " + after, after.isEmpty());
    }

    @Test
    public void testElapseCommandIsWiredToTheTimeService() {
        // The elapse command used to be built before the time service was assigned, so typing
        // elapse at the console threw a NullPointerException and ended the application.
        Interakt interakt = new Interakt();

        Assert.assertTrue(interakt.onCommand(new Console(), "elapse", new String[0]));
    }

    @Test
    public void testCreateUserPicksTheUserForEachSupportedMode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        Assert.assertTrue(Interakt.createUser(new String[] {"console"}, printStream) instanceof Console);
        Assert.assertTrue(Interakt.createUser(new String[] {"PLAYER"}, printStream) instanceof Player);
        Assert.assertEquals("", out.toString());
    }

    @Test
    public void testCreateUserRejectsAnUnsupportedMode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Assert.assertNull(Interakt.createUser(new String[] {"bogus"}, new PrintStream(out)));
        Assert.assertEquals("Unsupported mode entered. Supported modes: console, player", out.toString().trim());
    }

    @Test
    public void testCreateUserRejectsAMissingMode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Assert.assertNull(Interakt.createUser(new String[0], new PrintStream(out)));
        Assert.assertEquals("Missing required argument: <console|player>", out.toString().trim());
    }

    @Test
    public void testStartShutsDownWhenRunReturnsAtTheEndOfInput() {
        // Before start existed, main returned without saving or stopping anything when run hit
        // the end of input, and the auto-save thread kept the process alive.
        List<String> events = new ArrayList<>();
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Interakt interakt;
        try {
            interakt = new Interakt() {
                @Override
                public void onStartup() {
                    events.add("startup");
                }

                @Override
                public void shutdownApplication() {
                    events.add("shutdown");
                }
            };
        } finally {
            System.setIn(originalIn);
        }

        interakt.start(new Console() {
            @Override
            public void sendMessage(String message) {
                events.add("message");
            }
        });

        Assert.assertEquals(List.of("startup", "message", "shutdown"), events);
    }

    /**
     * The background services this application starts during onStartup are threads declared by the
     * application itself, so a live thread from the application's own package is evidence that
     * startup ran.
     */
    private Set<Thread> getLiveApplicationThreads() {
        Set<Thread> applicationThreads = new HashSet<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getClass().getName().startsWith("dansapps.interakt.")) {
                applicationThreads.add(thread);
            }
        }
        return applicationThreads;
    }
}
