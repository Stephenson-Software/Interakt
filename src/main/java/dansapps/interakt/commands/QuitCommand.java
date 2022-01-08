package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class QuitCommand extends AbstractCommand {

    public QuitCommand() {
        super(new ArrayList<>(List.of("quit")), new ArrayList<>(List.of("interakt.quit")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("Goodbye!");
        System.exit(0);
        return true;
    }

    @Override
    public boolean execute(AbstractCommandSender sender, String[] strings) {
        return execute(sender);
    }
}