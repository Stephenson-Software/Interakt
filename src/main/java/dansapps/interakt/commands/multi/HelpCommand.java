/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands.multi;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.users.Console;
import dansapps.interakt.users.Player;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class HelpCommand extends InteraktCommand {

    public HelpCommand() {
        super(new ArrayList<>(List.of("help", "h")), new ArrayList<>(List.of("interakt.help")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (sender instanceof Console) {
            sender.sendMessage("=== Console Commands ===");
            sender.sendMessage("help - View a list of useful commands.");
            sender.sendMessage("list - List entities and worlds.");
            sender.sendMessage("view <actor|world>- View an actor or world.");
            sender.sendMessage("relations <actor> - See the relations of an actor.");
            sender.sendMessage("create <actor|world> <name> - Create an actor or world.");
            sender.sendMessage("delete <actor|world> <name> - Delete an actor or world.");
            sender.sendMessage("place <actor> <world> - Place an actor in an world.");
            sender.sendMessage("generatetestdata - Generate test data.");
            sender.sendMessage("save - Force the application to save its data.");
            sender.sendMessage("info - View information about the application.");
            sender.sendMessage("stats - View statistics about the application.");
            sender.sendMessage("wipe - Wipe the data of the application.");
            sender.sendMessage("quit - Quit the application.");
        }
        else if (sender instanceof Player) {
            sender.sendMessage("=== Player Commands ===");
            sender.sendMessage("help - View a list of useful commands.");
            sender.sendMessage("save - Save application data.");
            sender.sendMessage("quit - Quit the application.");
        }

        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}