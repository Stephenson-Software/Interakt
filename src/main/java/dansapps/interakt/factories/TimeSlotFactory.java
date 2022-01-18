/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.TimePartition;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
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

    public void createTimePartition(int length) {
        TimePartition timePartition = new TimePartition(length);
        PersistentData.getInstance().addTimePartition(timePartition);
    }
}