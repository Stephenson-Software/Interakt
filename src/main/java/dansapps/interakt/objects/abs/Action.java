/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.abs;

import java.time.LocalDateTime;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 * @brief This class is intended to represent an action that an entity can take while in an environment.
 */
public abstract class Action {
    private String name;
    private LocalDateTime timestamp;

    public Action(String name) {
        this.name = name;
        timestamp = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}