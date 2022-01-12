/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Environment;
import dansapps.interakt.objects.domain.LivingEntity;
import preponderous.ponder.misc.ArgumentParser;
import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class CreateCommand extends ApplicationCommand {

    public CreateCommand() {
        super(new ArrayList<>(List.of("create")), new ArrayList<>(List.of("interakt.create")));
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
        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);
        if (doubleQuoteArgs.size() < 2) {
            sender.sendMessage("Arguments must be designated in between quotation marks.");
            return false;
        }
        String type = doubleQuoteArgs.get(0);
        String name = doubleQuoteArgs.get(1);

        if (type.equalsIgnoreCase("entity")) {
            LivingEntity entity = new LivingEntity(name); // TODO: create factory for this
            PersistentData.getInstance().addEntity(entity);
            sender.sendMessage("Entity created.");
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            Environment environment = new Environment(name, 10); // TODO: create factory for this
            PersistentData.getInstance().addEnvironment(environment);
            sender.sendMessage("Environment created.");
            return true;
        }
        else {
            sender.sendMessage("'" + type + "' is not a supported type. Supported types include entity and environment.");
            return false;
        }
    }
}