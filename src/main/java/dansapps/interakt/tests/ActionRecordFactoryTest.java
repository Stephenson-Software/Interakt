package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.objects.ActionRecord;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

/**
 * Characterization tests for ActionRecordFactory: a record is always kept in persistent data, and is
 * attached to its actor when that actor exists. A loaded record whose actor is gone (usually because
 * the actor died) is still kept in persistent data, but is not attached to anyone and nothing is thrown.
 */
public class ActionRecordFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory(persistentData);
    private final ActorFactory actorFactory = new ActorFactory(new EntityRecordFactory(logger, persistentData), logger,
            new EventFactory(), interakt, actionRecordFactory, persistentData, new FoodItemFactory(logger));

    @Test
    public void testCreateFromActorAddsRecordToPersistentDataAndActor() throws Exception {
        Actor actor = actorFactory.createActorWithName("Recorder");
        int recordsBefore = persistentData.getActionRecords().size();
        int actorRecordsBefore = actor.getNumActionRecords();

        actionRecordFactory.createActionRecord(actor, ACTIONTYPE.BEFRIEND);

        Assert.assertEquals(recordsBefore + 1, persistentData.getActionRecords().size());
        ActionRecord created = persistentData.getActionRecords().getLast();
        Assert.assertEquals(actor.getUUID(), created.getEntityUUID());
        Assert.assertEquals("befriend", created.getActionName());
        Assert.assertEquals(actorRecordsBefore + 1, actor.getNumActionRecords());
    }

    @Test
    public void testCreateFromDataAttachesRecordToLivingActor() throws Exception {
        Actor actor = actorFactory.createActorWithName("Loader");
        ActionRecord saved = new ActionRecord(actor.getUUID(), ACTIONTYPE.MOVE);
        int actorRecordsBefore = actor.getNumActionRecords();

        actionRecordFactory.createActionRecord(saved.save());

        ActionRecord loaded = persistentData.getActionRecords().getLast();
        Assert.assertEquals(saved.getUUID(), loaded.getUUID());
        Assert.assertEquals(actor.getUUID(), loaded.getEntityUUID());
        Assert.assertEquals("move", loaded.getActionName());
        Assert.assertEquals(saved.getTimestamp(), loaded.getTimestamp());
        Assert.assertEquals(actorRecordsBefore + 1, actor.getNumActionRecords());
    }

    @Test
    public void testCreateFromDataKeepsRecordOfMissingActorWithoutThrowing() {
        UUID missingActorUUID = UUID.randomUUID();
        Map<String, String> data = new ActionRecord(missingActorUUID, ACTIONTYPE.ATTACK).save();

        actionRecordFactory.createActionRecord(data);

        Assert.assertEquals(1, persistentData.getActionRecords().size());
        ActionRecord loaded = persistentData.getActionRecords().getFirst();
        Assert.assertEquals(missingActorUUID, loaded.getEntityUUID());
        Assert.assertEquals("attack", loaded.getActionName());
        Assert.assertEquals(0, persistentData.getNumActors());
    }

    @Test
    public void testCreateFromDataDoesNotAttachRecordToOtherActors() throws Exception {
        Actor bystander = actorFactory.createActorWithName("Bystander");
        int bystanderRecordsBefore = bystander.getNumActionRecords();
        Map<String, String> data = new ActionRecord(UUID.randomUUID(), ACTIONTYPE.REST).save();

        actionRecordFactory.createActionRecord(data);

        Assert.assertEquals(bystanderRecordsBefore, bystander.getNumActionRecords());
    }
}
