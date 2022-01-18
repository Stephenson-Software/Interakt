/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import preponderous.environmentlib.abs.objects.TimeSlot;
import preponderous.ponder.misc.abs.Savable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 * @brief This class is intended to represent a finite slot of time.
 */
public class TimePartition extends TimeSlot implements Savable {

    public TimePartition(int milliseconds) {
        super(milliseconds);
    }

    public TimePartition(Map<String, String> data) {
        super(-1);
        this.load(data);
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("timestamp", gson.toJson(getTimestamp().toString()));
        saveMap.put("milliseconds", gson.toJson(getMilliseconds()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        setTimestamp(LocalDateTime.parse(gson.fromJson(data.get("timestamp"), String.class)));
        setMilliseconds(Integer.parseInt(gson.fromJson(data.get("milliseconds"), String.class)));
    }
}