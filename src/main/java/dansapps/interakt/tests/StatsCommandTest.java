package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.StatsCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.TimePartition;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Characterization tests for StatsCommand. These assert the behavior the command has today,
 * including that it stops partway through and reports failure whenever no actor has acted or
 * explored, because the PersistentData lookups it relies on throw in that case.
 */
public class StatsCommandTest {
    private static final String FAILURE_MESSAGE = "Something went wrong when printing stats.";

    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory(persistentData);
    private final List<String> messages = new ArrayList<>();
    private final List<String> loggedErrors = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };
    private final Logger logger = new Logger(new Interakt()) {
        @Override
        public void logError(String errorMessage) {
            loggedErrors.add(errorMessage);
        }
    };
    private final StatsCommand statsCommand = new StatsCommand(logger, persistentData);

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
    public void testStatsReportsEveryLineWhenActorsHaveActedExploredAndBefriended() {
        World world = createWorld("Earth");
        Actor busy = createActor("Busy");
        Actor idle = createActor("Idle");
        actionRecordFactory.createActionRecord(busy, ACTIONTYPE.MOVE);
        actionRecordFactory.createActionRecord(busy, ACTIONTYPE.REST);
        actionRecordFactory.createActionRecord(idle, ACTIONTYPE.REST);
        busy.addSquareIfNotExplored(world.getRandomSquare(), new EventFactory());
        busy.setRelation(idle, 100);
        persistentData.addTimePartition(new TimePartition(150000));

        boolean success = statsCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertTrue(loggedErrors.isEmpty());
        Assert.assertEquals(Arrays.asList(
                "Number of actors: 2",
                "Number of worlds: 1",
                "Number of regions: " + persistentData.getRegions().size(),
                "Number of squares: " + persistentData.getSquares().size(),
                "Number of elapsed time partitions: 1",
                "Number of action records: 3",
                "Number of entity records: " + persistentData.getEntityRecords().size(),
                "Most active actor: Busy",
                "Least active actor: Idle",
                "Most well travelled: Busy",
                "Most friendly actor: Busy",
                "Minutes elapsed: 2"
        ), messages);
    }

    @Test
    public void testStatsReportsNotApplicableWhenNobodyHasAFriend() {
        World world = createWorld("Earth");
        Actor actor = createActor("Loner");
        actionRecordFactory.createActionRecord(actor, ACTIONTYPE.REST);
        actor.addSquareIfNotExplored(world.getRandomSquare(), new EventFactory());

        boolean success = statsCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertTrue(messages.contains("Most friendly actor: N/A"));
        Assert.assertEquals("Minutes elapsed: 0", messages.get(messages.size() - 1));
    }

    @Test
    public void testStatsWithNoActorsFailsAfterPrintingTheCounts() {
        boolean success = statsCommand.execute(console);

        Assert.assertFalse(success);
        Assert.assertEquals(Arrays.asList(FAILURE_MESSAGE), loggedErrors);
        Assert.assertEquals(7, messages.size());
        Assert.assertEquals("Number of actors: 0", messages.get(0));
        Assert.assertEquals("Number of entity records: 0", messages.get(6));
    }

    @Test
    public void testStatsFailsWhenActorsExistButNoneHasActed() {
        createActor("Newcomer");

        boolean success = statsCommand.execute(console);

        Assert.assertFalse(success);
        Assert.assertEquals(Arrays.asList(FAILURE_MESSAGE), loggedErrors);
        Assert.assertEquals("Number of actors: 1", messages.get(0));
        Assert.assertFalse(messages.contains("Most active actor: Newcomer"));
    }

    @Test
    public void testStatsFailsWhenActorsHaveActedButNoneHasExplored() {
        Actor actor = createActor("Homebody");
        actionRecordFactory.createActionRecord(actor, ACTIONTYPE.REST);

        boolean success = statsCommand.execute(console);

        Assert.assertFalse(success);
        Assert.assertEquals(Arrays.asList(FAILURE_MESSAGE), loggedErrors);
        Assert.assertTrue(messages.contains("Least active actor: Homebody"));
        Assert.assertFalse(messages.contains("Most well travelled: Homebody"));
    }

    @Test
    public void testStatsIgnoresArguments() {
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");
        actionRecordFactory.createActionRecord(actor, ACTIONTYPE.REST);
        actor.addSquareIfNotExplored(world.getRandomSquare(), new EventFactory());

        boolean success = statsCommand.execute(console, new String[]{"unexpected"});

        Assert.assertTrue(success);
        Assert.assertTrue(messages.contains("Most active actor: Gerald"));
    }
}
