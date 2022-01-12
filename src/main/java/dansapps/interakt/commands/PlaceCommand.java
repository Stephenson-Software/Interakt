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
public class PlaceCommand extends ApplicationCommand {

    public PlaceCommand() {
        super(new ArrayList<>(List.of("place")), new ArrayList<>(List.of("interakt.place")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Usage: place \"entity name\" \"environment name\"");
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
        String entityName = doubleQuoteArgs.get(0);
        LivingEntity entity = (LivingEntity) PersistentData.getInstance().getEntity(entityName);
        if (entity == null) {
            sender.sendMessage("That entity wasn't found.");
            return false;
        }
        if (entity.getLocation() != null) {
            sender.sendMessage("That entity is already in an environment.");
            return false;
        }
        String environmentName = doubleQuoteArgs.get(1);
        Environment environment = PersistentData.getInstance().getEnvironment(environmentName);
        if (environment == null) {
            sender.sendMessage("That environment wasn't found.");
            return false;
        }
        environment.addEntity(entity);
        sender.sendMessage(entity.getName() + " was placed in the " + environment.getName() + " environment.");
        return true;
    }
}