package dansapps.interakt.tests;

import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.objects.ActionRecord;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Characterization tests for ActionRecord: the action name each ACTIONTYPE is recorded under, and
 * that saving and loading a record keeps its UUIDs, action name and timestamp.
 */
public class ActionRecordTest {

    @Test
    public void testEachActionTypeIsRecordedUnderItsLowerCaseName() {
        UUID entityUUID = UUID.randomUUID();

        Assert.assertEquals("rest", new ActionRecord(entityUUID, ACTIONTYPE.REST).getActionName());
        Assert.assertEquals("attack", new ActionRecord(entityUUID, ACTIONTYPE.ATTACK).getActionName());
        Assert.assertEquals("befriend", new ActionRecord(entityUUID, ACTIONTYPE.BEFRIEND).getActionName());
        Assert.assertEquals("move", new ActionRecord(entityUUID, ACTIONTYPE.MOVE).getActionName());
        Assert.assertEquals("reproduce", new ActionRecord(entityUUID, ACTIONTYPE.REPRODUCE).getActionName());
    }

    @Test
    public void testNewRecordHasItsOwnUUIDAndTheCurrentTime() {
        UUID entityUUID = UUID.randomUUID();
        LocalDateTime before = LocalDateTime.now();

        ActionRecord record = new ActionRecord(entityUUID, ACTIONTYPE.MOVE);

        LocalDateTime after = LocalDateTime.now();
        Assert.assertEquals(entityUUID, record.getEntityUUID());
        Assert.assertNotNull(record.getUUID());
        Assert.assertFalse(entityUUID.equals(record.getUUID()));
        Assert.assertFalse(record.getTimestamp().isBefore(before));
        Assert.assertFalse(record.getTimestamp().isAfter(after));
    }

    @Test
    public void testEachRecordGetsADifferentUUID() {
        UUID entityUUID = UUID.randomUUID();

        ActionRecord first = new ActionRecord(entityUUID, ACTIONTYPE.REST);
        ActionRecord second = new ActionRecord(entityUUID, ACTIONTYPE.REST);

        Assert.assertFalse(first.getUUID().equals(second.getUUID()));
    }

    @Test
    public void testSaveAndLoadRoundTrip() {
        ActionRecord original = new ActionRecord(UUID.randomUUID(), ACTIONTYPE.BEFRIEND);

        Map<String, String> data = original.save();
        ActionRecord loaded = new ActionRecord(data);

        Assert.assertEquals(4, data.size());
        Assert.assertEquals(original.getUUID(), loaded.getUUID());
        Assert.assertEquals(original.getEntityUUID(), loaded.getEntityUUID());
        Assert.assertEquals("befriend", loaded.getActionName());
        Assert.assertEquals(original.getTimestamp(), loaded.getTimestamp());
    }

    @Test
    public void testSavedValuesAreJsonStrings() {
        ActionRecord record = new ActionRecord(UUID.randomUUID(), ACTIONTYPE.ATTACK);

        Map<String, String> data = record.save();

        Assert.assertEquals("\"" + record.getUUID() + "\"", data.get("uuid"));
        Assert.assertEquals("\"" + record.getEntityUUID() + "\"", data.get("entityUUID"));
        Assert.assertEquals("\"attack\"", data.get("actionName"));
        Assert.assertEquals("\"" + record.getTimestamp() + "\"", data.get("timestamp"));
    }
}
