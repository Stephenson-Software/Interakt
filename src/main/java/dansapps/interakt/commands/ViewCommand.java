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
public class ViewCommand extends InteraktCommand {

    public ViewCommand() {
        super(new ArrayList<>(List.of("view")), new ArrayList<>(List.of("interakt.view")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Usage: view \"type\" \"name\"");
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
            Actor actor;
            try {
                actor = PersistentData.getInstance().getEntity(name);
            } catch (Exception e) {
                sender.sendMessage("That entity wasn't found.");
                return false;
            }
            actor.sendInfo(sender);
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            World world;
            try {
                world = PersistentData.getInstance().getEnvironment(name);
            } catch (Exception e) {
                sender.sendMessage("That environment wasn't found.");
                return false;
            }
            world.sendInfo(sender);
            return true;
        }
        else {
            sender.sendMessage("That type isn't supported.");
            return false;
        }
    }
}