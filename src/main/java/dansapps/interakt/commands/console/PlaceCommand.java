/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands.console;

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
public class PlaceCommand extends InteraktCommand {
    private final PersistentData persistentData;

    public PlaceCommand(PersistentData persistentData) {
        super(new ArrayList<>(List.of("place", "pl")), new ArrayList<>(List.of("interakt.place")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Usage: place \"actor name\" \"world name\"");
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

        if (doubleQuoteArgs.size() < 2) {
            sender.sendMessage("Arguments must be designated in between quotation marks.");
            return false;
        }

        String actorName = doubleQuoteArgs.get(0);
        String worldName = doubleQuoteArgs.get(1);

        Actor actor;
        try {
            actor = persistentData.getActor(actorName);
        }
        catch (Exception e) {
            sender.sendMessage("That actor wasn't found.");
            return false;
        }

        if (actorIsAlreadyInAWorld(actor)) {
            sender.sendMessage("That actor is already in a world.");
            return false;
        }

        World world;
        try {
            world = persistentData.getWorld(worldName);
        } catch (Exception e) {
            sender.sendMessage("That world wasn't found.");
            return false;
        }

        return persistentData.placeIntoEnvironment(world, sender, actor);
    }

    private boolean actorIsAlreadyInAWorld(Actor actor) {
        return actor.getWorld() != null;
    }
}