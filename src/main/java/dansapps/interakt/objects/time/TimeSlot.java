/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.time;

import java.time.LocalDateTime;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 * @brief This class is intended to represent a finite slot of time.
 */
public class TimeSlot {
    private int milliseconds;
    private LocalDateTime timestamp = LocalDateTime.now();

    public TimeSlot(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isActive() {
        // TODO: implement
        return false;
    }
}