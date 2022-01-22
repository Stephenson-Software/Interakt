package dansapps.interakt.objects;

import java.time.LocalDateTime;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class Event {
    private String message;
    private LocalDateTime timestamp;

    public Event(String message) {
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}