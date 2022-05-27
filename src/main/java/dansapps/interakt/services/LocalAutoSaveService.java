package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.utils.Logger;

import java.util.concurrent.TimeUnit;

public class LocalAutoSaveService extends Thread {
    private Interakt interakt;
    private LocalStorageService storageService;
    private Logger logger;

    public LocalAutoSaveService(Interakt interakt, LocalStorageService storageService, Logger logger) {
        this.interakt = interakt;
        this.storageService = storageService;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (interakt.isRunning()) {
            storageService.save();
            logger.logInfo("Data has been saved. This will happen every " + CONFIG.SECONDS_BETWEEN_AUTO_SAVES + " seconds.");

            try {
                TimeUnit.SECONDS.sleep(CONFIG.SECONDS_BETWEEN_AUTO_SAVES);
            } catch (Exception e) {
                logger.logError("Local Auto Save Service was interrupted.");
            }
        }
    }
}