package dansapps.interakt.commands;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.LivingEntity;
import dansapps.interakt.objects.domain.Environment;
import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class ListCommand extends AbstractCommand {

    public ListCommand() {
        super(new ArrayList<>(List.of("list")), new ArrayList<>(List.of("interakt.list")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("=== Entities ===");
        for (LivingEntity entity : PersistentData.getInstance().getEntities()) {
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
    public boolean execute(AbstractCommandSender sender, String[] args) {
        return execute(sender);
    }
}