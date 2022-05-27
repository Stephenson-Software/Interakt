package dansapps.interakt.tests.utils;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.*;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;

public class TestUtilities {
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final PersistentData persistentData;
    private final EntityRecordFactory entityRecordFactory;
    private final EventFactory eventFactory;
    private final ActionRecordFactory actionRecordFactory;
    private final ActorFactory actorFactory;
    private final SquareFactory squareFactory;
    private final RegionFactory regionFactory;
    private final WorldFactory worldFactory;
    private final FoodItemFactory foodItemFactory;

    public TestUtilities(PersistentData persistentData) {
        this.persistentData = persistentData;
        entityRecordFactory = new EntityRecordFactory(logger, persistentData);
        eventFactory = new EventFactory();
        actionRecordFactory = new ActionRecordFactory(persistentData);
        foodItemFactory = new FoodItemFactory(logger);
        actorFactory = new ActorFactory(entityRecordFactory, logger, eventFactory, interakt, actionRecordFactory, persistentData, foodItemFactory);
        squareFactory = new SquareFactory(logger, persistentData);
        regionFactory = new RegionFactory(squareFactory, logger, persistentData);
        worldFactory = new WorldFactory(regionFactory, logger, persistentData);
    }

    public String wrapInQuotationMarks(String toWrap) {
        return "\"" + toWrap + "\"";
    }

    public void createActor(String name) {
        CreateCommand createCommand = new CreateCommand(actorFactory, worldFactory);
        Console console = new Console();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("actor");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(console, args);
    }

    public void createWorld(String name) {
        CreateCommand createCommand = new CreateCommand(actorFactory, worldFactory);
        Console console = new Console();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("world");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(console, args);
    }
}