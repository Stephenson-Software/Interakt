/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands.console;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.WorldFactory;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class CreateCommand extends InteraktCommand {
    private final ActorFactory actorFactory;
    private final WorldFactory worldFactory;

    public CreateCommand(ActorFactory actorFactory, WorldFactory worldFactory) {
        super(new ArrayList<>(List.of("create")), new ArrayList<>(List.of("interakt.create")));
        this.actorFactory = actorFactory;
        this.worldFactory = worldFactory;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Usage: create \"type\" \"name\"");
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

        if (type.equalsIgnoreCase("actor")) {
            try {
                actorFactory.createActorWithName(name);
            } catch (NameTakenException e) {
                sender.sendMessage("That name was taken.");
                return false;
            }
            sender.sendMessage("Actor created.");
            return true;
        }
        else if (type.equalsIgnoreCase("world")) {
            worldFactory.createWorld(name);
            sender.sendMessage("World created.");
            return true;
        }
        else {
            sender.sendMessage("'" + type + "' is not a supported type. Supported types include actor and world.");
            return false;
        }
    }
}