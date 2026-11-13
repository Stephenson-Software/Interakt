package dansapps.interakt.tests;

import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.objects.Event;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Characterization tests for EventFactory and Event: an event keeps its message unchanged and is
 * stamped with the time it was created, and every call produces a new event.
 */
public class EventFactoryTest {
    private final EventFactory eventFactory = new EventFactory();

    @Test
    public void testMessageIsKeptUnchanged() {
        Event event = eventFactory.createEvent("  Gerald has appeared at 2, 3.  ");

        Assert.assertEquals("  Gerald has appeared at 2, 3.  ", event.getMessage());
    }

    @Test
    public void testTimestampIsTheCreationTime() {
        LocalDateTime before = LocalDateTime.now();
        Event event = eventFactory.createEvent("sentinel");
        LocalDateTime after = LocalDateTime.now();

        Assert.assertNotNull(event.getTimestamp());
        Assert.assertFalse(event.getTimestamp().isBefore(before));
        Assert.assertFalse(event.getTimestamp().isAfter(after));
    }

    @Test
    public void testEachCallCreatesANewEvent() {
        Event first = eventFactory.createEvent("sentinel");
        Event second = eventFactory.createEvent("sentinel");

        Assert.assertNotSame(first, second);
    }

    @Test
    public void testNullMessageIsKept() {
        Assert.assertNull(eventFactory.createEvent(null).getMessage());
    }
}
