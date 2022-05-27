package dansapps.interakt.factories;

import dansapps.interakt.objects.Event;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class EventFactory {

    public Event createEvent(String message) {
        return new Event(message);
    }
}