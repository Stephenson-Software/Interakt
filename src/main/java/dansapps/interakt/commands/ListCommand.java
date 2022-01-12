package dansapps.interakt.commands;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;
import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class ListCommand extends ApplicationCommand {

    public ListCommand() {
        super(new ArrayList<>(List.of("list")), new ArrayList<>(List.of("interakt.list")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("=== Entities ===");
        for (Entity entity : PersistentData.getInstance().getEntities()) {
            sender.sendMessage(entity.getName());
        }
        sender.sendMessage("");
        sender.sendMessage("=== Environments ===");
        for (Environment environment : PersistentData.getInstance().getEnvironments()) {
            sender.sendMessage(environment.getName());
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}