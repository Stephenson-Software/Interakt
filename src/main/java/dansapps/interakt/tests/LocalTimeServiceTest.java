package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.factories.TimePartitionFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.services.LocalTimeService;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Characterization tests for LocalTimeService: each elapse adds one time partition of
 * TIME_SLOT_LENGTH_IN_SECONDS, removes actors that are dead and keeps the living ones, and run
 * does nothing once the application has stopped running.
 */
public class LocalTimeServiceTest {
    private final Interakt interakt = new Interakt();
    private final PersistentData persistentData = new PersistentData();
    private final List<String> loggedInfo = new ArrayList<>();
    private final Logger logger = new Logger(interakt) {
        @Override
        public void logInfo(String message) {
            loggedInfo.add(message);
        }
    };
    private final ActorFactory actorFactory = new ActorFactory(new EntityRecordFactory(logger, persistentData), logger,
            new EventFactory(), interakt, new ActionRecordFactory(persistentData), persistentData, new FoodItemFactory(logger));
    private final LocalTimeService timeService = new LocalTimeService(interakt, new TimePartitionFactory(persistentData), logger, persistentData);

    @Test
    public void testElapseAddsOneTimePartitionOfTheConfiguredLength() {
        timeService.elapse();

        Assert.assertEquals(1, persistentData.getTimePartitions().size());
        Assert.assertEquals(CONFIG.TIME_SLOT_LENGTH_IN_SECONDS * 1000, persistentData.getTimePartitions().get(0).getMilliseconds());
        Assert.assertEquals(CONFIG.TIME_SLOT_LENGTH_IN_SECONDS, persistentData.getSecondsElapsed());
    }

    @Test
    public void testEachElapseAddsAnotherPartition() {
        timeService.elapse();
        timeService.elapse();
        timeService.elapse();

        Assert.assertEquals(3, persistentData.getTimePartitions().size());
        Assert.assertEquals(3 * CONFIG.TIME_SLOT_LENGTH_IN_SECONDS, persistentData.getSecondsElapsed());
    }

    @Test
    public void testElapseLogsTheNumberOfElapsedSlots() {
        timeService.elapse();
        timeService.elapse();

        Assert.assertEquals("Time elapsed. Number of elapsed slots: 1", loggedInfo.get(1));
        Assert.assertEquals("Time elapsed. Number of elapsed slots: 2", loggedInfo.get(loggedInfo.size() - 1));
    }

    @Test
    public void testElapseRemovesDeadActorsAndKeepsLivingOnes() throws Exception {
        Actor dead = actorFactory.createActorWithName("Dead");
        Actor living = actorFactory.createActorWithName("Living");
        dead.setHealth(0);

        timeService.elapse();

        Assert.assertEquals(1, persistentData.getNumActors());
        Assert.assertFalse(persistentData.getActors().contains(dead));
        Assert.assertTrue(persistentData.getActors().contains(living));
    }

    @Test
    public void testRunDoesNothingWhenTheApplicationIsNotRunning() {
        interakt.setRunning(false);

        timeService.run();

        Assert.assertTrue(persistentData.getTimePartitions().isEmpty());
    }
}
