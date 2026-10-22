package dansapps.interakt.tests;

import dansapps.interakt.objects.TimePartition;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Characterization tests for TimePartition: its length in milliseconds and that saving and loading
 * a partition keeps both its length and its timestamp.
 */
public class TimePartitionTest {

    @Test
    public void testConstructorKeepsMilliseconds() {
        TimePartition partition = new TimePartition(1500);

        Assert.assertEquals(1500, partition.getMilliseconds());
        Assert.assertNotNull(partition.getTimestamp());
    }

    @Test
    public void testSaveAndLoadRoundTrip() {
        TimePartition original = new TimePartition(2750);

        Map<String, String> data = original.save();
        TimePartition loaded = new TimePartition(data);

        Assert.assertEquals(2, data.size());
        Assert.assertEquals("2750", data.get("milliseconds"));
        Assert.assertEquals("\"" + original.getTimestamp() + "\"", data.get("timestamp"));
        Assert.assertEquals(2750, loaded.getMilliseconds());
        Assert.assertEquals(original.getTimestamp(), loaded.getTimestamp());
    }

    @Test
    public void testLoadOverwritesExistingValues() {
        TimePartition source = new TimePartition(10);
        TimePartition target = new TimePartition(99);

        target.load(source.save());

        Assert.assertEquals(10, target.getMilliseconds());
        Assert.assertEquals(source.getTimestamp(), target.getTimestamp());
    }
}
