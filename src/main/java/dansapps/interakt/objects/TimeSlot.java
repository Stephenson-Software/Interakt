/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import preponderous.ponder.misc.abs.Savable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 * @brief This class is intended to represent a finite slot of time.
 */
public class TimeSlot implements Savable {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int milliseconds;

    public TimeSlot(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public TimeSlot(Map<String, String> data) {
        this.load(data);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public boolean isActive() {
        return LocalDateTime.now().isAfter(timestamp.plusSeconds(milliseconds/1000));
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("timestamp", gson.toJson(timestamp.toString()));
        saveMap.put("milliseconds", gson.toJson(milliseconds));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        timestamp = LocalDateTime.parse(gson.fromJson(data.get("timestamp"), String.class));
        milliseconds = Integer.parseInt(gson.fromJson(data.get("milliseconds"), String.class));
    }
}