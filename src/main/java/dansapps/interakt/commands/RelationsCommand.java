package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RelationsCommand extends InteraktCommand {

    public RelationsCommand() {
        super(new ArrayList<>(List.of("relations")), new ArrayList<>(List.of("interakt.relations")));
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
            actor = PersistentData.getInstance().getActor(actorName);
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