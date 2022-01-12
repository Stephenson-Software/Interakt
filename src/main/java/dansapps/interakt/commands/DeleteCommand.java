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
public class DeleteCommand extends ApplicationCommand {

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
            PersistentData.getInstance().removeEntity(entity);
            return true;
        }
        else if (type.equalsIgnoreCase("environment")) {
            Environment environment = PersistentData.getInstance().getEnvironment(name);
            PersistentData.getInstance().removeEnvironment(environment);
            return true;
        }
        else {
            sender.sendMessage("That type isn't supported.");
            return false;
        }
    }
}