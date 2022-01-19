/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.World;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class PlaceCommand extends InteraktCommand {

    public PlaceCommand() {
        super(new ArrayList<>(List.of("place")), new ArrayList<>(List.of("interakt.place")));
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

        String actorName = doubleQuoteArgs.get(0);
        Actor actor;
        try {
            actor = PersistentData.getInstance().getActor(actorName);
        }
        catch (Exception e) {
            sender.sendMessage("That entity wasn't found.");
            return false;
        }

        if (actorIsAlreadyInAWorld(actor)) {
            sender.sendMessage("That entity is already in an environment.");
            return false;
        }

        // place into environment
        World world;
        Square square;
        try {
            world = getWorld(doubleQuoteArgs, sender);
        } catch (Exception e) {
            sender.sendMessage("That environment wasn't found.");
            return false;
        }

        square = world.getFirstSquare();

        if (square == null) {
            sender.sendMessage("There was a problem finding a location in that environment to place the entity.");
            return false;
        }

        world.addEntity(actor);
        square.addActor(actor);
        sender.sendMessage(actor.getName() + " was placed in the " + world.getName() + " world at square " + square);
        return true;
    }

    private World getWorld(ArrayList<String> doubleQuoteArgs, CommandSender sender) throws Exception {
        String environmentName = doubleQuoteArgs.get(1);
        try {
            return PersistentData.getInstance().getWorld(environmentName);
        } catch (Exception e) {
            sender.sendMessage("That world wasn't found.");
            throw new Exception();
        }
    }

    private boolean actorIsAlreadyInAWorld(Actor actor) {
        return actor.getEnvironmentUUID() != null;
    }
}