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
    private final SquareFactory squareFactory = new SquareFactory(logger);
    private final RegionFactory regionFactory = new RegionFactory(squareFactory, logger);
    private final WorldFactory worldFactory = new WorldFactory(regionFactory, logger);
    private final EventFactory eventFactory = new EventFactory();
    private final EntityRecordFactory entityRecordFactory = new EntityRecordFactory(logger);
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory();
    private final ActorFactory actorFactory = new ActorFactory(entityRecordFactory, logger, eventFactory, interakt, actionRecordFactory);

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory);

        Console console = new Console();
        generateTestDataCommand.execute(console);

        Assert.assertTrue(PersistentData.getInstance().getNumWorlds() > 0);
        Assert.assertTrue(PersistentData.getInstance().getNumActors() > 0);
    }
}