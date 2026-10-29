package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Characterization tests for ActorFactory's name handling and the genealogy and relations it sets up
 * when an actor is created from two parents. These assert the behavior the code has today, including
 * that a name stays taken while its entity record exists, even after the actor itself is removed.
 */
public class ActorFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final ActorFactory actorFactory = new ActorFactory(new EntityRecordFactory(logger, persistentData), logger,
            new EventFactory(), interakt, new ActionRecordFactory(persistentData), persistentData, new FoodItemFactory(logger));

    @Test
    public void testCreateActorWithNameRegistersActorAndEntityRecord() throws Exception {
        Actor actor = actorFactory.createActorWithName("Registered");

        Assert.assertEquals("Registered", actor.getName());
        Assert.assertSame(actor, persistentData.getActor("Registered"));
        EntityRecord record = persistentData.getEntityRecord(actor.getUUID());
        Assert.assertEquals("Registered", record.getName());
        Assert.assertEquals(1, persistentData.getNumActors());
        Assert.assertEquals(1, persistentData.getEntityRecords().size());
    }

    @Test
    public void testCreateActorWithNameRejectsTakenName() throws Exception {
        actorFactory.createActorWithName("Duplicate");

        try {
            actorFactory.createActorWithName("Duplicate");
            Assert.fail("A second actor with a taken name was created.");
        } catch (NameTakenException expected) {
            // expected
        }

        Assert.assertEquals(1, persistentData.getNumActors());
        Assert.assertEquals(1, persistentData.getEntityRecords().size());
    }

    @Test
    public void testNameStaysTakenWhileEntityRecordRemains() throws Exception {
        Actor removed = actorFactory.createActorWithName("Departed");
        persistentData.removeActor(removed);

        try {
            actorFactory.createActorWithName("Departed");
            Assert.fail("The name of a removed actor with a remaining entity record was reused.");
        } catch (NameTakenException expected) {
            // expected
        }

        Assert.assertEquals(0, persistentData.getNumActors());
    }

    @Test
    public void testCreateActorWithRandomNameUsesFiveLowercaseLetters() throws Exception {
        Actor actor = actorFactory.createActorWithRandomName();

        Assert.assertTrue(actor.getName(), actor.getName().matches("[a-z]{5}"));
        Assert.assertSame(actor, persistentData.getActor(actor.getName()));
    }

    @Test
    public void testCreateActorFromParentsLinksGenealogy() throws Exception {
        Actor parent1 = actorFactory.createActorWithName("ParentOne");
        Actor parent2 = actorFactory.createActorWithName("ParentTwo");

        Actor child = actorFactory.createActorFromParents(parent1, parent2);

        Assert.assertEquals(2, child.getParentUUIDs().size());
        Assert.assertTrue(child.getParentUUIDs().contains(parent1.getUUID()));
        Assert.assertTrue(child.getParentUUIDs().contains(parent2.getUUID()));
        Assert.assertTrue(child.getChildUUIDs().isEmpty());
        Assert.assertEquals(1, parent1.getChildUUIDs().size());
        Assert.assertTrue(parent1.getChildUUIDs().contains(child.getUUID()));
        Assert.assertEquals(1, parent2.getChildUUIDs().size());
        Assert.assertTrue(parent2.getChildUUIDs().contains(child.getUUID()));
        Assert.assertSame(child, persistentData.getActor(child.getUUID()));
        Assert.assertEquals(3, persistentData.getNumActors());
    }

    @Test
    public void testCreateActorFromParentsSetsMaximumRelationsBothWays() throws Exception {
        Actor parent1 = actorFactory.createActorWithName("MotherActor");
        Actor parent2 = actorFactory.createActorWithName("FatherActor");

        Actor child = actorFactory.createActorFromParents(parent1, parent2);

        Assert.assertEquals(100, child.getRelation(parent1));
        Assert.assertEquals(100, child.getRelation(parent2));
        Assert.assertEquals(100, parent1.getRelation(child));
        Assert.assertEquals(100, parent2.getRelation(child));
        Assert.assertEquals(0, parent1.getRelation(parent2));
    }
}
