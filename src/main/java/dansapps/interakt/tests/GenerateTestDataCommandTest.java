package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.GenerateTestDataCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.*;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateTestDataCommandTest {
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final PersistentData persistentData = new PersistentData();
    private final SquareFactory squareFactory = new SquareFactory(logger, persistentData);
    private final RegionFactory regionFactory = new RegionFactory(squareFactory, logger, persistentData);
    private final WorldFactory worldFactory = new WorldFactory(regionFactory, logger, persistentData);
    private final EventFactory eventFactory = new EventFactory();
    private final EntityRecordFactory entityRecordFactory = new EntityRecordFactory(logger, persistentData);
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory(persistentData);
    private final FoodItemFactory foodItemFactory = new FoodItemFactory(logger);
    private final ActorFactory actorFactory = new ActorFactory(entityRecordFactory, logger, eventFactory, interakt, actionRecordFactory, persistentData, foodItemFactory);

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);

        Console console = new Console();
        generateTestDataCommand.execute(console);

        Assert.assertTrue(persistentData.getNumWorlds() > 0);
        Assert.assertTrue(persistentData.getNumActors() > 0);
    }

    @Test
    public void testGenerateTestDataCommandReportsAlreadyGeneratedOnSecondRun() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);
        generateTestDataCommand.execute(new Console());
        int numActors = persistentData.getNumActors();
        List<String> messages = new ArrayList<>();

        boolean success = generateTestDataCommand.execute(recordingConsole(messages));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Test data has already been generated."), messages);
        Assert.assertEquals(1, persistentData.getNumWorlds());
        Assert.assertEquals(numActors, persistentData.getNumActors());
    }

    @Test
    public void testGenerateTestDataCommandReportsAlreadyGeneratedAfterTestActorIsDeleted() throws Exception {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);
        generateTestDataCommand.execute(new Console());
        persistentData.removeActor(persistentData.getActor("Adam"));
        int numActors = persistentData.getNumActors();
        List<String> messages = new ArrayList<>();

        boolean success = generateTestDataCommand.execute(recordingConsole(messages));

        // The entity record keeps a deleted actor's name taken, so the actor is not regenerated.
        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Test data has already been generated."), messages);
        Assert.assertEquals(numActors, persistentData.getNumActors());
    }

    @Test
    public void testGenerateTestDataCommandPlacesActorsIntoExistingTestWorld() throws Exception {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);
        worldFactory.createWorld("Test");
        List<String> messages = new ArrayList<>();

        boolean success = generateTestDataCommand.execute(recordingConsole(messages));

        Assert.assertTrue(success);
        Assert.assertEquals("Test data has been generated.", messages.get(messages.size() - 1));
        Assert.assertEquals(1, persistentData.getNumWorlds());
        Assert.assertEquals(persistentData.getWorld("Test"), persistentData.getActor("Adam").getWorld());
    }

    @Test
    public void testGenerateTestDataCommandReportsGeneratedWhenOnlyTestWorldIsCreated() throws Exception {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);
        generateTestDataCommand.execute(new Console());
        persistentData.removeWorld(persistentData.getWorld("Test"));
        List<String> messages = new ArrayList<>();

        boolean success = generateTestDataCommand.execute(recordingConsole(messages));

        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList("Test data has been generated."), messages);
        Assert.assertTrue(persistentData.isWorld("Test"));
    }

    private Console recordingConsole(List<String> messages) {
        return new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
    }
}