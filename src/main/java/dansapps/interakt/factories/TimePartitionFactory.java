/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.TimePartition;

import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class TimePartitionFactory {
    private final PersistentData persistentData;

    public TimePartitionFactory(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public void createTimePartition(int length) {
        TimePartition timePartition = new TimePartition(length);
        persistentData.addTimePartition(timePartition);
    }

    public void createTimePartition(Map<String, String> data) {
        TimePartition timePartition = new TimePartition(data);
        persistentData.addTimePartition(timePartition);
    }
}