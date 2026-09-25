package dansapps.interakt.tests;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ActorNotFoundException;
import dansapps.interakt.exceptions.EntityRecordNotFoundException;
import dansapps.interakt.exceptions.ZeroFriendshipsExistentException;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.TimePartition;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Characterization tests for PersistentData's lookups, removal, placement and statistics. These
 * assert the behavior the code has today, including the lookups that differ in case sensitivity
 * and in how a miss is reported.
 */
public class PersistentDataTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory(persistentData);

    private Actor createActor(String name) {
        testUtilities.createActor(name);
        try {
            return persistentData.getActor(name);
        } catch (Exception e) {
            Assert.fail("Actor " + name + " was not found after creation.");
            return null;
        }
    }

    private World createWorld(String name) {
        testUtilities.createWorld(name);
        try {
            return persistentData.getWorld(name);
        } catch (Exception e) {
            Assert.fail("World " + name + " was not found after creation.");
            return null;
        }
    }

    @Test
    public void testGetActorByNameIgnoresCase() throws ActorNotFoundException {
        Actor actor = createActor("Gerald");

        Assert.assertSame(actor, persistentData.getActor("gERALD"));
    }

    @Test
    public void testGetActorByUUIDFindsTheActor() throws ActorNotFoundException {
        Actor actor = createActor("Gerald");

        Assert.assertSame(actor, persistentData.getActor(actor.getUUID()));
    }

    @Test(expected = ActorNotFoundException.class)
    public void testGetActorByUnknownNameThrows() throws ActorNotFoundException {
        persistentData.getActor("Nobody");
    }

    @Test(expected = ActorNotFoundException.class)
    public void testGetActorByUnknownUUIDThrows() throws ActorNotFoundException {
        persistentData.getActor(UUID.randomUUID());
    }

    @Test
    public void testGetWorldByNameIgnoresCase() throws Exception {
        World world = createWorld("Earth");

        Assert.assertSame(world, persistentData.getWorld("eARTH"));
    }

    @Test(expected = Exception.class)
    public void testGetWorldByUnknownNameThrows() throws Exception {
        persistentData.getWorld("Nowhere");
    }

    @Test
    public void testGetWorldByUnknownUUIDReturnsNullInsteadOfThrowing() {
        createWorld("Earth");

        Assert.assertNull(persistentData.getWorld(UUID.randomUUID()));
    }

    @Test
    public void testIsWorld() {
        createWorld("Earth");

        Assert.assertTrue(persistentData.isWorld("earth"));
        Assert.assertFalse(persistentData.isWorld("Mars"));
    }

    @Test
    public void testIsActorIgnoresCaseWhileTheActorExists() {
        createActor("Gerald");

        Assert.assertTrue(persistentData.isActor("GERALD"));
        Assert.assertFalse(persistentData.isActor("Bernard"));
    }

    @Test
    public void testIsActorStillMatchesTheExactNameOfARemovedActor() {
        Actor actor = createActor("Gerald");

        persistentData.removeActor(actor);

        Assert.assertEquals(0, persistentData.getNumActors());
        Assert.assertTrue("The entity record outlives the actor, so its exact name stays taken.",
                persistentData.isActor("Gerald"));
        Assert.assertFalse("The entity record lookup is case-sensitive.",
                persistentData.isActor("gerald"));
    }

    @Test
    public void testGetEntityRecordByNameIsCaseSensitive() throws EntityRecordNotFoundException {
        Actor actor = createActor("Gerald");

        Assert.assertEquals(actor.getUUID(), persistentData.getEntityRecord("Gerald").getUUID());
        try {
            persistentData.getEntityRecord("gerald");
            Assert.fail("A differently-cased name is not expected to match an entity record.");
        } catch (EntityRecordNotFoundException ignored) {

        }
    }

    @Test
    public void testGetEntityRecordByUUID() throws EntityRecordNotFoundException {
        Actor actor = createActor("Gerald");

        Assert.assertEquals("Gerald", persistentData.getEntityRecord(actor.getUUID()).getName());
    }

    @Test
    public void testPlaceIntoEnvironmentAddsTheActorToTheWorldAndASquare() {
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");

        Assert.assertTrue(persistentData.placeIntoEnvironment(world, actor));

        Assert.assertTrue(world.isEntityPresent(actor));
        Assert.assertEquals(world.getUUID(), actor.getWorld().getUUID());
        Square square = actor.getSquare();
        Assert.assertNotNull(square);
        Assert.assertTrue(square.isActorPresent(actor));
    }

    @Test
    public void testPlaceIntoEnvironmentRefusesAnActorThatIsAlreadyPlaced() {
        World earth = createWorld("Earth");
        World mars = createWorld("Mars");
        Actor actor = createActor("Gerald");
        persistentData.placeIntoEnvironment(earth, actor);

        Assert.assertFalse(persistentData.placeIntoEnvironment(mars, actor));

        Assert.assertEquals(earth.getUUID(), actor.getWorld().getUUID());
        Assert.assertFalse(mars.isEntityPresent(actor));
    }

    @Test
    public void testPlaceIntoEnvironmentReportsTheOutcomeToTheSender() {
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");

        persistentData.placeIntoEnvironment(world, console, actor);
        persistentData.placeIntoEnvironment(world, console, actor);

        Assert.assertEquals(2, messages.size());
        Assert.assertEquals("Gerald was placed in the Earth world.", messages.get(0));
        Assert.assertEquals("A problem occurred during placement.", messages.get(1));
    }

    @Test
    public void testRemoveActorRemovesItFromItsWorldAndSquare() {
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");
        persistentData.placeIntoEnvironment(world, actor);
        Square square = actor.getSquare();

        persistentData.removeActor(actor);

        Assert.assertFalse(persistentData.getActors().contains(actor));
        Assert.assertFalse(world.isEntityPresent(actor));
        Assert.assertFalse(square.isActorPresent(actor));
    }

    @Test
    public void testRemoveDeadActorsRemovesOnlyActorsAtOrBelowZeroHealth() {
        Actor alive = createActor("Alive");
        Actor dead = createActor("Dead");
        dead.setHealth(0);

        persistentData.removeDeadActors();

        Assert.assertTrue(persistentData.getActors().contains(alive));
        Assert.assertFalse(persistentData.getActors().contains(dead));
    }

    @Test
    public void testGetActorWithMostAndLeastActionRecords() {
        Actor busy = createActor("Busy");
        Actor idle = createActor("Idle");
        Actor middling = createActor("Middling");
        for (int i = 0; i < 3; i++) {
            actionRecordFactory.createActionRecord(busy, ACTIONTYPE.MOVE);
        }
        actionRecordFactory.createActionRecord(middling, ACTIONTYPE.MOVE);

        Assert.assertSame(busy, persistentData.getActorWithMostActionRecords());
        Assert.assertSame(idle, persistentData.getActorWithLeastActionRecords());
        Assert.assertEquals(4, persistentData.getActionRecords().size());
    }

    @Test(expected = NullPointerException.class)
    public void testGetActorWithMostActionRecordsThrowsWhenNoActorHasAny() {
        createActor("Idle");

        persistentData.getActorWithMostActionRecords();
    }

    @Test(expected = NullPointerException.class)
    public void testGetActorWithLeastActionRecordsThrowsWhenThereAreNoActors() {
        persistentData.getActorWithLeastActionRecords();
    }

    @Test(expected = NullPointerException.class)
    public void testGetMostWellTravelledActorThrowsWhenNoActorHasExplored() {
        createActor("Homebody");

        persistentData.getMostWellTravelledActor();
    }

    @Test
    public void testGetMostFriendlyActor() throws ZeroFriendshipsExistentException {
        Actor popular = createActor("Popular");
        Actor friendly = createActor("Friendly");
        Actor other = createActor("Other");
        popular.setRelation(friendly, 100);
        popular.setRelation(other, 100);
        friendly.setRelation(popular, 100);

        Assert.assertSame(popular, persistentData.getMostFriendlyActor());
    }

    @Test(expected = ZeroFriendshipsExistentException.class)
    public void testGetMostFriendlyActorThrowsWhenNobodyHasAFriend() throws ZeroFriendshipsExistentException {
        Actor actor = createActor("Acquaintance");
        Actor other = createActor("Other");
        actor.setRelation(other, 50);

        persistentData.getMostFriendlyActor();
    }

    @Test
    public void testGetSecondsElapsedTruncatesTheTotalOfAllTimePartitions() {
        persistentData.addTimePartition(new TimePartition(1500));
        persistentData.addTimePartition(new TimePartition(1499));

        Assert.assertEquals(2, persistentData.getSecondsElapsed());
    }

    @Test
    public void testClearDataEmptiesEveryCollection() {
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");
        persistentData.placeIntoEnvironment(world, actor);
        actionRecordFactory.createActionRecord(actor, ACTIONTYPE.MOVE);
        persistentData.addTimePartition(new TimePartition(1000));

        persistentData.clearData();

        Assert.assertEquals(0, persistentData.getNumActors());
        Assert.assertEquals(0, persistentData.getNumWorlds());
        Assert.assertTrue(persistentData.getRegions().isEmpty());
        Assert.assertTrue(persistentData.getSquares().isEmpty());
        Assert.assertTrue(persistentData.getTimePartitions().isEmpty());
        Assert.assertTrue(persistentData.getActionRecords().isEmpty());
        Assert.assertTrue(persistentData.getEntityRecords().isEmpty());
        Assert.assertFalse(persistentData.isActor("Gerald"));
    }
}
