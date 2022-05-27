/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands.multi;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.abs.InteraktCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class QuitCommand extends InteraktCommand {
    private final Interakt interakt;

    public QuitCommand(Interakt interakt) {
        super(new ArrayList<>(List.of("quit", "exit")), new ArrayList<>(List.of("interakt.quit")));
        this.interakt = interakt;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Goodbye!");
        interakt.shutdownApplication();
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}