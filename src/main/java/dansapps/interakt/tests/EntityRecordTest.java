package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for EntityRecord's persistence round trip and what loading malformed data does: the failure
 * is logged rather than thrown, whether the record was built from values or from a data map, so one
 * malformed saved record does not stop the records after it from loading.
 */
public class EntityRecordTest {
    private final Logger logger = new Logger(new Interakt());
    private final List<String> loggedErrors = new ArrayList<>();

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
        EntityRecord loaded = new EntityRecord(data, logger);

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

    @Test
    public void testConstructingFromMalformedDataLogsInsteadOfThrowing() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", "\"not-a-uuid\"");
        data.put("name", "\"Bertha\"");

        new EntityRecord(data, getRecordingLogger());

        Assert.assertEquals(1, loggedErrors.size());
        Assert.assertEquals("Something went wrong loading an actor record.", loggedErrors.get(0));
    }

    @Test
    public void testFactoryKeepsCreatingRecordsAfterMalformedData() {
        PersistentData persistentData = new PersistentData();
        EntityRecordFactory factory = new EntityRecordFactory(getRecordingLogger(), persistentData);
        Map<String, String> malformed = new HashMap<>();
        malformed.put("uuid", "\"not-a-uuid\"");
        malformed.put("name", "\"Bertha\"");
        EntityRecord valid = new EntityRecord(UUID.randomUUID(), "Gerald", logger);

        factory.createEntityRecord(malformed);
        factory.createEntityRecord(valid.save());

        Assert.assertEquals(1, loggedErrors.size());
        boolean validRecordLoaded = false;
        for (EntityRecord record : persistentData.getEntityRecords()) {
            if (valid.getUUID().equals(record.getUUID()) && "Gerald".equals(record.getName())) {
                validRecordLoaded = true;
            }
        }
        Assert.assertTrue(validRecordLoaded);
    }

    private Logger getRecordingLogger() {
        return new Logger(new Interakt()) {
            @Override
            public void logError(String errorMessage) {
                loggedErrors.add(errorMessage);
            }
        };
    }
}
