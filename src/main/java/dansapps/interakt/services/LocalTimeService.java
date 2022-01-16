/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.TimeSlotFactory;
import dansapps.interakt.objects.Entity;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalTimeService extends Thread {
    private static LocalTimeService instance;

    public static LocalTimeService getInstance() {
        if (instance == null) {
            instance = new LocalTimeService();
        }
        return instance;
    }

    @Override
    public void run() {
        while (Interakt.getInstance().isRunning()) {
            elapse();
            try {
                TimeUnit.SECONDS.sleep(CONFIG.TIME_SLOT_LENGTH_IN_SECONDS);
            } catch (Exception e) {
                Logger.getInstance().log("Time stream was interrupted.");
            }
        }
    }

    public void forceElapse() {
        elapse();
    }

    private void elapse() {
        Logger.getInstance().log("----------------------");
        int TIME_SLOT_LENGTH_IN_MILLISECONDS = CONFIG.TIME_SLOT_LENGTH_IN_SECONDS * 1000;
        TimeSlotFactory.getInstance().createTimeSlot(TIME_SLOT_LENGTH_IN_MILLISECONDS);
        makeEntitiesPerformMoveAction();
        Logger.getInstance().log("Time elapsed. Number of elapsed slots: " + PersistentData.getInstance().getTimeSlots().size());
    }

    private void makeEntitiesPerformMoveAction() {
        for (Entity entity : PersistentData.getInstance().getEntities()) {
            entity.attemptToPerformMoveAction();
        }
    }
}