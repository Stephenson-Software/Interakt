package dansapps.interakt.services;

import dansapps.interakt.objects.time.TimeSlot;
import dansapps.interakt.objects.time.TimeStream;
import dansapps.interakt.utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalTimeService extends Thread {
    private static LocalTimeService instance;

    private TimeStream timeStream = new TimeStream();
    private int timeSlotLength = 1000;

    private LocalTimeService() {

    }

    public static LocalTimeService getInstance() {
        if (instance == null) {
            instance = new LocalTimeService();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            elapse();
            try {
                TimeUnit.SECONDS.sleep(timeSlotLength/1000);
            } catch (Exception e) {
                Logger.getInstance().log("Time stream was interrupted.");
            }
        }
    }

    public TimeStream getTimeStream() {
        return timeStream;
    }

    public void setTimeStream(TimeStream timeStream) {
        this.timeStream = timeStream;
    }

    public int getTimeSlotLength() {
        return timeSlotLength;
    }

    public void setTimeSlotLength(int timeSlotLength) {
        this.timeSlotLength = timeSlotLength;
    }

    private void elapse() {
        Logger.getInstance().log("Time elapsing.");
        TimeSlot timeSlot = new TimeSlot(timeSlotLength);
        timeStream.addTimeSlot(timeSlot);
    }

    /**
     * Method for testing.
     */
    public static void main(String[] args) {
        LocalTimeService.getInstance().start();
    }
}