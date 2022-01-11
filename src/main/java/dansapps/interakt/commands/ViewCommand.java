/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Environment;
import dansapps.interakt.objects.domain.LivingEntity;
import preponderous.ponder.misc.ArgumentParser;
import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class ViewCommand extends AbstractCommand {

    public ViewCommand() {
        super(new ArrayList<>(List.of("view")), new ArrayList<>(List.of("interakt.view")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("Usage: view \"type\" \"name\"");
        return false;
    }

    @Override
    public boolean execute(AbstractCommandSender sender, String[] args) {
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
            LivingEntity entity = (LivingEntity) PersistentData.getInstance().getEntity(name);
            entity.sendInfo(sender);
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            Environment environment = PersistentData.getInstance().getEnvironment(name);
            environment.sendInfo(sender);
            return true;
        }
        else {
            sender.sendMessage("That type isn't supported.");
            return false;
        }
    }
}