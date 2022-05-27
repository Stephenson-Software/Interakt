package dansapps.interakt.tests;

import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EntityRecordFactory;
import dansapps.interakt.factories.WorldFactory;
import dansapps.interakt.users.Console;

public class TestUtilities {
    private static TestUtilities instance;
    private EntityRecordFactory entityRecordFactory;
    private ActorFactory actorFactory;
    private WorldFactory worldFactory;

    private TestUtilities() {
        this.entityRecordFactory = new EntityRecordFactory();
        this.actorFactory = new ActorFactory(entityRecordFactory);
        this.worldFactory = new WorldFactory();
    }

    public static TestUtilities getInstance() {
        if (instance == null) {
            instance = new TestUtilities();
        }
        return instance;
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