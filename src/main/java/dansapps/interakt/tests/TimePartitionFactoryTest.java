package dansapps.interakt.tests;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.TimePartitionFactory;
import dansapps.interakt.objects.TimePartition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Characterization tests for TimePartitionFactory: both a new partition and one loaded from saved data
 * are appended to persistent data in creation order, and their lengths are what the elapsed-time total
 * is summed from.
 */
public class TimePartitionFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final TimePartitionFactory timePartitionFactory = new TimePartitionFactory(persistentData);

    @Test
    public void testCreateFromLengthAppendsPartition() {
        timePartitionFactory.createTimePartition(1200);

        Assert.assertEquals(1, persistentData.getTimePartitions().size());
        TimePartition created = persistentData.getTimePartitions().get(0);
        Assert.assertEquals(1200, created.getMilliseconds());
        Assert.assertNotNull(created.getTimestamp());
    }

    @Test
    public void testCreateFromDataKeepsLengthAndTimestamp() {
        TimePartition saved = new TimePartition(4321);

        timePartitionFactory.createTimePartition(saved.save());

        Assert.assertEquals(1, persistentData.getTimePartitions().size());
        TimePartition loaded = persistentData.getTimePartitions().get(0);
        Assert.assertEquals(4321, loaded.getMilliseconds());
        Assert.assertEquals(saved.getTimestamp(), loaded.getTimestamp());
    }

    @Test
    public void testPartitionsAreKeptInCreationOrder() {
        timePartitionFactory.createTimePartition(100);
        timePartitionFactory.createTimePartition(new TimePartition(200).save());
        timePartitionFactory.createTimePartition(300);

        Assert.assertEquals(3, persistentData.getTimePartitions().size());
        Assert.assertEquals(100, persistentData.getTimePartitions().get(0).getMilliseconds());
        Assert.assertEquals(200, persistentData.getTimePartitions().get(1).getMilliseconds());
        Assert.assertEquals(300, persistentData.getTimePartitions().get(2).getMilliseconds());
    }

    @Test
    public void testSecondsElapsedSumsCreatedPartitions() {
        timePartitionFactory.createTimePartition(1500);
        timePartitionFactory.createTimePartition(new TimePartition(2600).save());

        Assert.assertEquals(4, persistentData.getSecondsElapsed());
    }
}
