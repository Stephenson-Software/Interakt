package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super(new ArrayList<>(List.of("help")), new ArrayList<>(List.of("interakt.help")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("=== Example Ponder Application Commands ===");
        sender.sendMessage("help - View a list of useful commands.");
        sender.sendMessage("list - List entities and environments.");
        sender.sendMessage("view - View an entity or environment.");
        sender.sendMessage("create - Create an entity or environment.");
        sender.sendMessage("delete - Delete an entity or environment.");
        sender.sendMessage("place - Place an entity in an environment.");
        sender.sendMessage("info - View information about the application.");
        sender.sendMessage("quit - Quit the application.");
        return true;
    }

    @Override
    public boolean execute(AbstractCommandSender sender, String[] args) {
        return execute(sender);
    }
}