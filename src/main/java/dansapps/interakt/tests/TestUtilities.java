package dansapps.interakt.tests;

import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.users.Console;

public class TestUtilities {
    private static TestUtilities instance;

    private TestUtilities() {

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
        CreateCommand createCommand = new CreateCommand();
        Console console = new Console();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("actor");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(console, args);
    }

    public void createWorld(String name) {
        CreateCommand createCommand = new CreateCommand();
        Console console = new Console();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("world");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(console, args);
    }
}