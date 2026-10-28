package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Characterization tests for Actor's health and relation logic. These assert the behavior the code
 * has today, including its clamping boundaries and the read-that-writes behavior of getRelation.
 * Also covers rebuilding an actor from its saved data.
 */
public class ActorTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);

    private Actor createActor(String name) {
        testUtilities.createActor(name);
        try {
            return persistentData.getActor(name);
        } catch (Exception e) {
            Assert.fail("Actor " + name + " was not found after creation.");
            return null;
        }
    }

    @Test
    public void testNewActorStartsAtMaxHealth() {
        Actor actor = createActor("HealthyActor");

        Assert.assertEquals(CONFIG.MAX_HEALTH, actor.getMaxHealth(), 0.0);
        Assert.assertEquals(actor.getMaxHealth(), actor.getHealth(), 0.0);
    }

    @Test
    public void testSetHealthClampsAtMaxHealth() {
        Actor actor = createActor("OverhealedActor");

        actor.setHealth(actor.getMaxHealth() * 5);

        Assert.assertEquals(actor.getMaxHealth(), actor.getHealth(), 0.0);
    }

    @Test
    public void testSetHealthPermitsNegativeValues() {
        Actor actor = createActor("WoundedActor");

        actor.setHealth(-42);

        Assert.assertEquals(-42, actor.getHealth(), 0.0);
    }

    @Test
    public void testIsDeadIsTrueAtOrBelowZeroHealth() {
        Actor actor = createActor("DyingActor");

        actor.setHealth(1);
        Assert.assertFalse(actor.isDead());

        actor.setHealth(0);
        Assert.assertTrue(actor.isDead());

        actor.setHealth(-1);
        Assert.assertTrue(actor.isDead());
    }

    @Test
    public void testGetRelationReturnsZeroForUnknownActor() {
        Actor actor = createActor("LonelyActor");
        Actor stranger = createActor("StrangerActor");

        Assert.assertEquals(0, actor.getRelation(stranger));
    }

    @Test
    public void testGetRelationInsertsAnEntryForAnUnknownActor() {
        Actor actor = createActor("ForgetfulActor");
        Actor stranger = createActor("UnknownActor");

        Assert.assertFalse(actor.save().get("relations").contains(stranger.getUUID().toString()));

        actor.getRelation(stranger);

        Assert.assertTrue("Reading a relation is expected to insert a zeroed entry.",
                actor.save().get("relations").contains(stranger.getUUID().toString()));
    }

    @Test
    public void testIncreaseRelationClampsAtOneHundred() {
        Actor actor = createActor("AdmiringActor");
        Actor target = createActor("AdmiredActor");

        actor.increaseRelation(target, 150);

        Assert.assertEquals(100, actor.getRelation(target));
    }

    @Test
    public void testDecreaseRelationClampsAtNegativeOneHundred() {
        Actor actor = createActor("SpitefulActor");
        Actor target = createActor("DespisedActor");

        actor.decreaseRelation(target, 150);

        Assert.assertEquals(-100, actor.getRelation(target));
    }

    @Test
    public void testIsFriendRequiresARelationAboveFifty() {
        Actor actor = createActor("PickyActor");
        Actor acquaintance = createActor("AcquaintanceActor");
        Actor friend = createActor("FriendActor");

        actor.setRelation(acquaintance, 50);
        actor.setRelation(friend, 51);

        Assert.assertFalse(actor.isFriend(acquaintance));
        Assert.assertTrue(actor.isFriend(friend));
    }

    @Test
    public void testGetNumFriendsCountsOnlyRelationsAboveFifty() {
        Actor actor = createActor("SociableActor");
        Actor enemy = createActor("EnemyActor");
        Actor acquaintance = createActor("NeutralActor");
        Actor barelyAFriend = createActor("BarelyFriendActor");
        Actor friend = createActor("CloseFriendActor");

        actor.setRelation(enemy, -100);
        actor.setRelation(acquaintance, 50);
        actor.setRelation(barelyAFriend, 51);
        actor.setRelation(friend, 100);

        Assert.assertEquals(2, actor.getNumFriends());
    }

    @Test
    public void testUnplacedActorIsRebuiltFromItsSavedData() throws Exception {
        Actor unplaced = createActor("UnplacedActor");
        Assert.assertNull(unplaced.getEnvironmentUUID());
        PersistentData loadedData = new PersistentData();
        List<String> loggedErrors = new ArrayList<>();

        createActorFactory(loadedData, getRecordingLogger(loggedErrors)).createActorWithData(unplaced.save());

        Actor loaded = loadedData.getActor("UnplacedActor");
        Assert.assertEquals(unplaced.getUUID(), loaded.getUUID());
        Assert.assertNull(loaded.getEnvironmentUUID());
        Assert.assertNull(loaded.getLocationUUID());
        Assert.assertEquals(2, loggedErrors.size());
        Assert.assertEquals("An environment wasn't found for UnplacedActor", loggedErrors.get(0));
        Assert.assertEquals("A location wasn't found for UnplacedActor", loggedErrors.get(1));
    }

    @Test
    public void testFactoryKeepsCreatingActorsAfterAnUnplacedActor() throws Exception {
        Actor unplaced = createActor("FirstUnplacedActor");
        Actor next = createActor("SecondActor");
        PersistentData loadedData = new PersistentData();
        ActorFactory factory = createActorFactory(loadedData, getRecordingLogger(new ArrayList<>()));

        factory.createActorWithData(unplaced.save());
        factory.createActorWithData(next.save());

        Assert.assertEquals(next.getUUID(), loadedData.getActor("SecondActor").getUUID());
    }

    private ActorFactory createActorFactory(PersistentData data, Logger logger) {
        Interakt interakt = new Interakt();
        return new ActorFactory(new EntityRecordFactory(logger, data), logger, new EventFactory(), interakt,
                new ActionRecordFactory(data), data, new FoodItemFactory(logger));
    }

    private Logger getRecordingLogger(List<String> loggedErrors) {
        return new Logger(new Interakt()) {
            @Override
            public void logError(String errorMessage) {
                loggedErrors.add(errorMessage);
            }
        };
    }
}
