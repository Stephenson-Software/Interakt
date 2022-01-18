/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
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
            PersistentData.getInstance().deleteEntity(name, sender);
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            PersistentData.getInstance().deleteEnvironment(name, sender);
            return true;
        }
        else {
            sender.sendMessage("That type isn't supported.");
            return false;
        }
    }

    private void deleteEntity(String name, CommandSender sender) {
        Actor actor;
        try {
            actor = PersistentData.getInstance().getActor(name);
        } catch (Exception e) {
            sender.sendMessage("That entity wasn't found.");
            return;
        }
        PersistentData.getInstance().removeActor(actor);
        sender.sendMessage("Entity removed.");
    }

    private void deleteEnvironment(String name, CommandSender sender) {
        World world;
        try {
            world = PersistentData.getInstance().getWorld(name);
        } catch (Exception e) {
            sender.sendMessage("That environment wasn't found.");
            return;
        }
        PersistentData.getInstance().removeWorld(world);
        sender.sendMessage("Environment removed.");
    }
}