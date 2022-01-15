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
public class PlaceCommand extends InteraktCommand {

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

        ArrayList<String> doubleQuoteArgs;
        try {
            doubleQuoteArgs = extractArgumentsInsideDoubleQuotes(args);
        }
        catch(Exception e) {
            sender.sendMessage("Arguments must be designated in between quotation marks.");
            return false;
        }

        String entityName = doubleQuoteArgs.get(0);
        Entity entity;
        try {
            entity = PersistentData.getInstance().getEntity(entityName);
        }
        catch (Exception e) {
            sender.sendMessage("That entity wasn't found.");
            return false;
        }

        if (entityIsAlreadyInAnEnvironment(entity)) {
            sender.sendMessage("That entity is already in an environment.");
            return false;
        }

        Environment environment;
        try {
            environment = getEnvironment(doubleQuoteArgs, sender);
        } catch (Exception e) {
            sender.sendMessage("That environment wasn't found.");
            return false;
        }

        environment.addEntity(entity);
        sender.sendMessage(entity.getName() + " was placed in the " + environment.getName() + " environment.");
        return true;
    }

    private Environment getEnvironment(ArrayList<String> doubleQuoteArgs, CommandSender sender) throws Exception {
        String environmentName = doubleQuoteArgs.get(1);
        try {
            return PersistentData.getInstance().getEnvironment(environmentName);
        } catch (Exception e) {
            sender.sendMessage("That environment wasn't found.");
            throw new Exception();
        }
    }

    private boolean entityIsAlreadyInAnEnvironment(Entity entity) {
        return entity.getEnvironmentUUID() != null;
    }
}