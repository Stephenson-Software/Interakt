package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.services.LocalAutoSaveService;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Characterization tests for LocalAutoSaveService: while the application is running it saves, logs
 * that it did, and waits SECONDS_BETWEEN_AUTO_SAVES; an interrupted wait is logged as an error rather
 * than ending the thread, and the loop ends once the application stops running. The storage service
 * is replaced by one that only counts saves, so nothing is written to disk.
 */
public class LocalAutoSaveServiceTest {
    private final Interakt interakt = new Interakt();
    private final List<String> loggedInfo = Collections.synchronizedList(new ArrayList<>());
    private final List<String> loggedErrors = Collections.synchronizedList(new ArrayList<>());
    private final Logger logger = new Logger(interakt) {
        @Override
        public void logInfo(String message) {
            loggedInfo.add(message);
        }

        @Override
        public void logError(String errorMessage) {
            loggedErrors.add(errorMessage);
        }
    };
    private final CountDownLatch saved = new CountDownLatch(1);
    private final AtomicInteger saves = new AtomicInteger();
    private final LocalStorageService storageService = new LocalStorageService(null, null, null, null, null, null, null, logger, new PersistentData()) {
        @Override
        public void save() {
            saves.incrementAndGet();
            saved.countDown();
        }
    };
    private final LocalAutoSaveService autoSaveService = new LocalAutoSaveService(interakt, storageService, logger);

    @Test
    public void testRunDoesNotSaveWhenTheApplicationIsNotRunning() {
        interakt.setRunning(false);

        autoSaveService.run();

        Assert.assertEquals(0, saves.get());
        Assert.assertTrue(loggedInfo.isEmpty());
    }

    @Test
    public void testSavesLogsAndStopsAfterAnInterruptedWaitOnceNotRunning() throws Exception {
        autoSaveService.start();
        Assert.assertTrue("The first save did not happen", saved.await(10, TimeUnit.SECONDS));

        interakt.setRunning(false);
        autoSaveService.interrupt();
        autoSaveService.join(TimeUnit.SECONDS.toMillis(10));

        Assert.assertFalse("The auto save thread did not stop", autoSaveService.isAlive());
        Assert.assertEquals(1, saves.get());
        Assert.assertEquals(1, loggedInfo.size());
        Assert.assertEquals("Data has been saved. This will happen every " + CONFIG.SECONDS_BETWEEN_AUTO_SAVES + " seconds.", loggedInfo.get(0));
        Assert.assertEquals(1, loggedErrors.size());
        Assert.assertEquals("Local Auto Save Service was interrupted.", loggedErrors.get(0));
    }
}
