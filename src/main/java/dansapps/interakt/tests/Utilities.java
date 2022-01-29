package dansapps.interakt.tests;

import dansapps.interakt.commands.CreateCommand;
import dansapps.interakt.misc.User;

public class Utilities {
    private static Utilities instance;

    private Utilities() {

    }

    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    public String wrapInQuotationMarks(String toWrap) {
        return "\"" + toWrap + "\"";
    }

    public void createActor(String name) {
        CreateCommand createCommand = new CreateCommand();
        User user = new User();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("actor");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(user, args);
    }

    public void createWorld(String name) {
        CreateCommand createCommand = new CreateCommand();
        User user = new User();
        String[] args = new String[2];
        args[0] = wrapInQuotationMarks("world");
        args[1] = wrapInQuotationMarks(name);
        createCommand.execute(user, args);
    }
}