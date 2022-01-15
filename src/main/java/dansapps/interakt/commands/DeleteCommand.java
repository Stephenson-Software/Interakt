/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class DeleteCommand extends InteraktCommand {

    public DeleteCommand() {
        super(new ArrayList<>(List.of("delete")), new ArrayList<>(List.of("interakt.delete")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Usage: delete \"type\" \"name\"");
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Not enough arguments.");
            return false;
        }

        ArrayList<String> doubleQuoteArgs;
        try {
            doubleQuoteArgs = extractArgumentsInsideDoubleQuotes(args);
        }
        catch(Exception e) {
            sender.sendMessage("Arguments must be designated in between quotation marks.");
            return false;
        }

        String type = doubleQuoteArgs.get(0);
        String name = doubleQuoteArgs.get(1);

        if (type.equalsIgnoreCase("entity")) {
            deleteEntity(name, sender);
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            deleteEnvironment(name, sender);
            return true;
        }
        else {
            sender.sendMessage("That type isn't supported.");
            return false;
        }
    }



    private void deleteEntity(String name, CommandSender sender) {
        Entity entity = PersistentData.getInstance().getEntity(name);
        PersistentData.getInstance().removeEntity(entity);
        sender.sendMessage("Entity removed.");
    }

    private void deleteEnvironment(String name, CommandSender sender) {
        Environment environment = PersistentData.getInstance().getEnvironment(name);
        PersistentData.getInstance().removeEnvironment(environment);
        sender.sendMessage("Environment removed.");
    }
}