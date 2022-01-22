package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.utils.Logger;

import java.util.concurrent.TimeUnit;

public class LocalAutoSaveService extends Thread {
    private static LocalAutoSaveService instance;

    private LocalAutoSaveService() {

    }

    public static LocalAutoSaveService getInstance() {
        if (instance == null) {
            instance = new LocalAutoSaveService();
        }
        return instance;
    }

    @Override
    public void run() {
        while (Interakt.getInstance().isRunning()) {
            LocalStorageService.getInstance().save();
            Logger.getInstance().logInfo("Data has been saved.");

            try {
                TimeUnit.SECONDS.sleep(CONFIG.SECONDS_BETWEEN_AUTO_SAVES);
            } catch (Exception e) {
                Logger.getInstance().logError("Local Auto Save Service was interrupted.");
            }
        }
    }
}