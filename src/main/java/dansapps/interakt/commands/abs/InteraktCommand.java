package dansapps.interakt.commands.abs;

import preponderous.ponder.misc.ArgumentParser;
import preponderous.ponder.system.abs.ApplicationCommand;

import java.util.ArrayList;

public abstract class InteraktCommand extends ApplicationCommand {

    public InteraktCommand(ArrayList<String> names, ArrayList<String> permissions) {
        super(names, permissions);
    }

    public ArrayList<String> extractArgumentsInsideDoubleQuotes(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);
        if (doubleQuoteArgs.size() < 2) {
            throw new Exception();
        }
        return doubleQuoteArgs;
    }
}
