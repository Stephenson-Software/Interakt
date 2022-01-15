/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.TimeSlotFactory;
import dansapps.interakt.objects.TimeSlot;
import dansapps.interakt.utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalTimeService extends Thread {
    private static LocalTimeService instance;
    private int timeSlotLength;

    private LocalTimeService() {
        int seconds = 60;
        timeSlotLength = seconds * 1000;
    }

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
                TimeUnit.SECONDS.sleep(timeSlotLength/1000);
            } catch (Exception e) {
                Logger.getInstance().log("Time stream was interrupted.");
            }
        }
    }

    public int getTimeSlotLength() {
        return timeSlotLength;
    }

    public void setTimeSlotLength(int timeSlotLength) {
        this.timeSlotLength = timeSlotLength;
    }

    private void elapse() {
        TimeSlotFactory.getInstance().createTimeSlot(timeSlotLength);
        Logger.getInstance().log("Time elapsed. Number of elapsed slots: " + PersistentData.getInstance().getTimeSlots().size());
    }

    /**
     * Method for testing.
     */
    public static void main(String[] args) {
        LocalTimeService.getInstance().start();
    }
}