package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.GenerateTestDataCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.*;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

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
    private final ActorFactory actorFactory = new ActorFactory(entityRecordFactory, logger, eventFactory, interakt, actionRecordFactory, persistentData);

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory, persistentData);

        Console console = new Console();
        generateTestDataCommand.execute(console);

        Assert.assertTrue(persistentData.getNumWorlds() > 0);
        Assert.assertTrue(persistentData.getNumActors() > 0);
    }
}