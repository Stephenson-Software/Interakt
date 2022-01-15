package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.TimeSlot;

public class TimeSlotFactory {
    private static TimeSlotFactory instance;

    private TimeSlotFactory() {

    }

    public static TimeSlotFactory getInstance() {
        if (instance == null) {
            instance = new TimeSlotFactory();
        }
        return instance;
    }

    public void createTimeSlot(int length) {
        TimeSlot timeSlot = new TimeSlot(length);
        PersistentData.getInstance().addTimeSlot(timeSlot);
    }
}