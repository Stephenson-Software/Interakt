package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class InfoCommand extends AbstractCommand {
    public InfoCommand() {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("interakt.info")));
    }

    @Override
    public boolean execute(AbstractCommandSender abstractCommandSender) {
        abstractCommandSender.sendMessage("=== Example Ponder Application Info ===");
        abstractCommandSender.sendMessage("Developer: Daniel Stephenson");
        abstractCommandSender.sendMessage("Developed with: Ponder");
        return true;
    }

    @Override
    public boolean execute(AbstractCommandSender abstractCommandSender, String[] strings) {
        return execute(abstractCommandSender);
    }
}
