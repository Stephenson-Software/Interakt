package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import dansapps.interakt.users.abs.CommandSenderImpl;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Characterization tests for Actor's health and relation logic. These assert the behavior the code
 * has today, including its clamping boundaries and the read-that-writes behavior of getRelation.
 * Also covers rebuilding an actor from its saved data, dead targets, unplaced actors, and the
 * messages sent to the player.
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

    @Test
    public void testToStringReportsNoWorldWhenItsWorldNoLongerExists() {
        Actor actor = createActor("StrandedActor");
        actor.setEnvironmentUUID(UUID.randomUUID());

        Assert.assertTrue(actor.toString().contains("World: N/A"));
    }

    @Test
    public void testReproduceCreatesNoOffspringWhenItsWorldNoLongerExists() throws Exception {
        testUtilities.createWorld("Earth");
        World world = persistentData.getWorld("Earth");
        Actor parent = createActor("StrandedParent");
        Actor partner = createActor("StrandedPartner");
        persistentData.placeIntoEnvironment(world, parent);
        persistentData.placeIntoEnvironment(world, partner);
        parent.getSquare().removeActor(parent);
        partner.getSquare().addActor(parent);
        parent.increaseRelation(partner, 100);
        parent.setEnvironmentUUID(UUID.randomUUID());
        int actorsBefore = persistentData.getActors().size();

        // The partner is picked at random from the square, so give every attempt a chance to find them.
        for (int attempt = 0; attempt < 200; attempt++) {
            parent.reproduce();
        }

        Assert.assertEquals(actorsBefore, persistentData.getActors().size());
    }

    private void placeOnSameSquare(Actor first, Actor second) throws Exception {
        testUtilities.createWorld("Earth");
        World world = persistentData.getWorld("Earth");
        persistentData.placeIntoEnvironment(world, first);
        persistentData.placeIntoEnvironment(world, second);
        first.getSquare().removeActor(first);
        second.getSquare().addActor(first);
    }

    @Test
    public void testAttackIgnoresADeadTarget() throws Exception {
        Actor attacker = createActor("Attacker");
        Actor corpse = createActor("Corpse");
        placeOnSameSquare(attacker, corpse);
        corpse.setHealth(0);
        Square square = corpse.getSquare();

        // The target is picked at random from the square, so give every attempt a chance to find them.
        for (int attempt = 0; attempt < 200; attempt++) {
            attacker.attack();
        }

        Assert.assertEquals(0, attacker.getNumActionRecords());
        Assert.assertEquals(0, corpse.getHealth(), 0.0);
        Assert.assertEquals("No second food item is expected on the square.", 2, square.getNumEntities());
    }

    @Test
    public void testBefriendIgnoresADeadTarget() throws Exception {
        Actor mourner = createActor("Mourner");
        Actor corpse = createActor("Corpse");
        placeOnSameSquare(mourner, corpse);
        corpse.setHealth(0);

        for (int attempt = 0; attempt < 200; attempt++) {
            mourner.befriend();
        }

        Assert.assertEquals(0, mourner.getNumActionRecords());
    }

    @Test
    public void testReproduceIgnoresADeadTarget() throws Exception {
        Actor widow = createActor("Widow");
        Actor corpse = createActor("Corpse");
        placeOnSameSquare(widow, corpse);
        widow.increaseRelation(corpse, 100);
        corpse.setHealth(0);
        int actorsBefore = persistentData.getActors().size();

        for (int attempt = 0; attempt < 200; attempt++) {
            widow.reproduce();
        }

        Assert.assertEquals(actorsBefore, persistentData.getActors().size());
    }

    @Test
    public void testAttackStillReachesALivingTarget() throws Exception {
        Actor attacker = createActor("Attacker");
        Actor target = createActor("Target");
        placeOnSameSquare(attacker, target);

        for (int attempt = 0; attempt < 200 && attacker.getNumActionRecords() == 0; attempt++) {
            attacker.attack();
        }

        Assert.assertEquals(1, attacker.getNumActionRecords());
    }

    @Test
    public void testUnplacedActorIsReportedWithoutAnError() {
        List<String> loggedErrors = new ArrayList<>();
        Actor actor = createActorFactoryActor("Unplaced", persistentData, getRecordingLogger(loggedErrors), new Interakt());

        String details = actor.toString();

        Assert.assertTrue(details, details.contains("Square: N/A"));
        Assert.assertEquals(Collections.emptyList(), loggedErrors);
    }

    @Test
    public void testUnplacedActorTakesItsTurnWithoutAnError() {
        List<String> loggedErrors = new ArrayList<>();
        Actor actor = createActorFactoryActor("Unplaced", persistentData, getRecordingLogger(loggedErrors), new Interakt());

        actor.move();
        actor.befriend();
        actor.attack();
        actor.reproduce();

        Assert.assertEquals(Collections.emptyList(), loggedErrors);
        Assert.assertEquals(0, actor.getNumActionRecords());
    }

    @Test
    public void testLocationOfASquareThatDoesNotExistIsStillAnError() {
        List<String> loggedErrors = new ArrayList<>();
        Actor actor = createActorFactoryActor("Stranded", persistentData, getRecordingLogger(loggedErrors), new Interakt());
        actor.setLocationUUID(UUID.randomUUID());

        String details = actor.toString();

        Assert.assertTrue(details, details.contains("Square: N/A"));
        Assert.assertEquals(Collections.singletonList("Location for Stranded was not found."), loggedErrors);
    }

    @Test
    public void testPlayerIsToldWhoAttackedThem() throws Exception {
        List<String> messages = new ArrayList<>();
        Interakt interakt = getInteraktWithPlayer("Hero", messages);
        Logger logger = new Logger(interakt);
        Actor attacker = createActorFactoryActor("Villain", persistentData, logger, interakt);
        Actor hero = createActorFactoryActor("Hero", persistentData, logger, interakt);
        placeOnSameSquare(attacker, hero);

        for (int attempt = 0; attempt < 200 && messages.isEmpty(); attempt++) {
            attacker.attack();
        }

        Assert.assertEquals(messages.toString(), 1, messages.size());
        Assert.assertTrue(messages.get(0), messages.get(0).matches("Villain attacked you and dealt \\d+ damage\\."));
    }

    @Test
    public void testPlayerIsToldWhoReproducedWithThem() throws Exception {
        List<String> messages = new ArrayList<>();
        Interakt interakt = getInteraktWithPlayer("Hero", messages);
        Logger logger = new Logger(interakt);
        Actor partner = createActorFactoryActor("Partner", persistentData, logger, interakt);
        Actor hero = createActorFactoryActor("Hero", persistentData, logger, interakt);
        placeOnSameSquare(partner, hero);
        partner.increaseRelation(hero, 100);

        for (int attempt = 0; attempt < 200 && messages.isEmpty(); attempt++) {
            partner.reproduce();
        }

        Assert.assertEquals(messages.toString(), 1, messages.size());
        Assert.assertTrue(messages.get(0), messages.get(0).matches("Partner reproduced with you, resulting in [a-z]+ coming into existence\\."));
    }

    private Actor createActorFactoryActor(String name, PersistentData data, Logger logger, Interakt interakt) {
        ActorFactory factory = new ActorFactory(new EntityRecordFactory(logger, data), logger, new EventFactory(), interakt,
                new ActionRecordFactory(data), data, new FoodItemFactory(logger));
        try {
            return factory.createActorWithName(name);
        } catch (NameTakenException e) {
            Assert.fail("The name " + name + " was already taken.");
            return null;
        }
    }

    private Interakt getInteraktWithPlayer(String playerActorName, List<String> messages) {
        Console player = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
        return new Interakt() {
            @Override
            public String getPlayerActorName() {
                return playerActorName;
            }

            @Override
            public CommandSenderImpl getCommandSender() {
                return player;
            }
        };
    }
}
