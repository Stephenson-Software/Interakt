package dansapps.interakt.commands;


import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class InfoCommand extends ApplicationCommand {
    public InfoCommand() {
        super(new ArrayList<>(List.of("info")), new ArrayList<>(List.of("interakt.info")));
    }

    @Override
    public boolean execute(CommandSender abstractCommandSender) {
        abstractCommandSender.sendMessage("=== Interakt Info ===");
        abstractCommandSender.sendMessage("Developer: Daniel Stephenson");
        abstractCommandSender.sendMessage("Developed with: Ponder");
        return true;
    }

    @Override
    public boolean execute(CommandSender abstractCommandSender, String[] strings) {
        return execute(abstractCommandSender);
    }
}
