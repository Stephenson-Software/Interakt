package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Characterization tests for EntityRecord's persistence round trip and what loading malformed data
 * does today: a record built with a logger logs the failure and keeps its values, while a record
 * built straight from a data map has no logger, so the failure surfaces as a NullPointerException.
 */
public class EntityRecordTest {
    private final Logger logger = new Logger(new Interakt());

    @Test
    public void testConstructorKeepsUUIDAndName() {
        UUID uuid = UUID.randomUUID();

        EntityRecord record = new EntityRecord(uuid, "Gerald", logger);

        Assert.assertEquals(uuid, record.getUUID());
        Assert.assertEquals("Gerald", record.getName());
    }

    @Test
    public void testSaveAndLoadRoundTrip() {
        EntityRecord original = new EntityRecord(UUID.randomUUID(), "Gerald", logger);

        Map<String, String> data = original.save();
        EntityRecord loaded = new EntityRecord(data);

        Assert.assertEquals(2, data.size());
        Assert.assertEquals("\"" + original.getUUID() + "\"", data.get("uuid"));
        Assert.assertEquals("\"Gerald\"", data.get("name"));
        Assert.assertEquals(original.getUUID(), loaded.getUUID());
        Assert.assertEquals("Gerald", loaded.getName());
    }

    @Test
    public void testLoadOverwritesExistingValues() {
        EntityRecord source = new EntityRecord(UUID.randomUUID(), "Gerald", logger);
        EntityRecord target = new EntityRecord(UUID.randomUUID(), "Bertha", logger);

        target.load(source.save());

        Assert.assertEquals(source.getUUID(), target.getUUID());
        Assert.assertEquals("Gerald", target.getName());
    }

    @Test
    public void testLoadingMalformedDataLogsAndKeepsExistingValues() {
        UUID uuid = UUID.randomUUID();
        EntityRecord record = new EntityRecord(uuid, "Gerald", logger);
        Map<String, String> data = new HashMap<>();
        data.put("uuid", "\"not-a-uuid\"");
        data.put("name", "\"Bertha\"");

        record.load(data);

        Assert.assertEquals(uuid, record.getUUID());
        Assert.assertEquals("Gerald", record.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructingFromMalformedDataThrowsBecauseThereIsNoLogger() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", "\"not-a-uuid\"");
        data.put("name", "\"Bertha\"");

        new EntityRecord(data);
    }
}
