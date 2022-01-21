/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class HelpCommand extends InteraktCommand {

    public HelpCommand() {
        super(new ArrayList<>(List.of("help")), new ArrayList<>(List.of("interakt.help")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("=== Example Ponder Application Commands ===");
        sender.sendMessage("help - View a list of useful commands.");
        sender.sendMessage("list - List entities and worlds.");
        sender.sendMessage("view - View an actor or world.");
        sender.sendMessage("create - Create an actor or world.");
        sender.sendMessage("delete - Delete an actor or world.");
        sender.sendMessage("place - Place an actor in an world.");
        sender.sendMessage("generatetestdata - Generate test data.");
        sender.sendMessage("save - Force the application to save its data.");
        sender.sendMessage("info - View information about the application.");
        sender.sendMessage("stats - View statistics about the application.");
        sender.sendMessage("wipe - Wipe the data of the application.");
        sender.sendMessage("quit - Quit the application.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}