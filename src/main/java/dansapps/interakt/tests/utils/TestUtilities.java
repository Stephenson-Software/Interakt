package dansapps.interakt.tests.utils;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.factories.*;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;

public class TestUtilities {
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final EntityRecordFactory entityRecordFactory = new EntityRecordFactory(logger);
    private final EventFactory eventFactory = new EventFactory();
    private final ActionRecordFactory actionRecordFactory = new ActionRecordFactory();
    private final ActorFactory actorFactory = new ActorFactory(entityRecordFactory, logger, eventFactory, interakt, actionRecordFactory);
    private final SquareFactory squareFactory = new SquareFactory(logger);
    private final RegionFactory regionFactory = new RegionFactory(squareFactory, logger);
    private final WorldFactory worldFactory = new WorldFactory(regionFactory, logger);

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