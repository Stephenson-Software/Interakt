package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
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
