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

    public void createTimePartition(int length) {
        TimePartition timePartition = new TimePartition(length);
        PersistentData.getInstance().addTimePartition(timePartition);
    }

    public void createTimePartition(Map<String, String> data) {
        TimePartition timePartition = new TimePartition(data);
        PersistentData.getInstance().addTimePartition(timePartition);
    }
}