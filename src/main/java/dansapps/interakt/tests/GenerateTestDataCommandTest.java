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
    public void testGenerateTestDataCommandReusesExistingTestWorld() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);
        generateTestDataCommand.execute(new Console());
        int numActors = persistentData.getNumActors();
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };

        boolean success = generateTestDataCommand.execute(console);

        // Every test actor's name is already taken on a second run, so each is skipped and the command
        // still reports success rather than "Test data has already been generated.".
        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList("Test data has been generated."), messages);
        Assert.assertEquals(1, persistentData.getNumWorlds());
        Assert.assertEquals(numActors, persistentData.getNumActors());
    }
}