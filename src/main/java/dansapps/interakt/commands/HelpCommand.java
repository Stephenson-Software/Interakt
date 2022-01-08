package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("interakt.help")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("=== Example Ponder Application Commands ===");
        sender.sendMessage("help - View a list of useful commands.");
        sender.sendMessage("create - Create an entity or environment.");
        sender.sendMessage("delete - Delete an entity or environment.");
        sender.sendMessage("view - View an entity or environment.");
        sender.sendMessage("info - View information about the application.");
        sender.sendMessage("quit - Quit the application.");
        return true;
    }

    @Override
    public boolean execute(AbstractCommandSender sender, String[] args) {
        return execute(sender);
    }
}