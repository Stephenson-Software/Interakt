package dansapps.interakt.factories;

import dansapps.interakt.objects.Event;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class EventFactory {
    private static EventFactory instance;

    private EventFactory() {

    }

    public static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public Event createEvent(String message) {
        return new Event(message);
    }
}