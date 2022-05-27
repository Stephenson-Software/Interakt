package dansapps.interakt.commands.console;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RelationsCommand extends InteraktCommand {
    private final PersistentData persistentData;

    public RelationsCommand(PersistentData persistentData) {
        super(new ArrayList<>(List.of("relations", "rel")), new ArrayList<>(List.of("interakt.relations")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage("Usage: relations \"actor\"");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        ArrayList<String> doubleQuoteArgs;
        try {
            doubleQuoteArgs = extractArgumentsInsideDoubleQuotes(args);
        }
        catch(Exception e) {
            commandSender.sendMessage("Actor must be designated in between quotation marks.");
            return false;
        }

        String actorName = doubleQuoteArgs.get(0);
        Actor actor;
        try {
            actor = persistentData.getActor(actorName);
        } catch (Exception e) {
            commandSender.sendMessage("That actor wasn't found.");
            return false;
        }

        String relationsString = actor.getRelationsString();

        commandSender.sendMessage("=== Relations ===");
        commandSender.sendMessage(relationsString);
        return true;
    }
}