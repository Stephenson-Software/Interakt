/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.services.LocalTimeService;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class ElapseCommand extends InteraktCommand {

    public ElapseCommand() {
        super(new ArrayList<>(List.of("elapse")), new ArrayList<>(List.of("interakt.elapse")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Forcing time elapse.");
        LocalTimeService.getInstance().elapse();
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}