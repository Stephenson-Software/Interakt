package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class DeleteCommand extends AbstractCommand {

    public DeleteCommand() {
        super(new ArrayList<>(Arrays.asList("delete")), new ArrayList<>(Arrays.asList("interakt.delete")));
    }

    @Override
    public boolean execute(AbstractCommandSender sender) {
        sender.sendMessage("This command isn't implemented yet.");
        // TODO: implement
        return false;
    }

    @Override
    public boolean execute(AbstractCommandSender sender, String[] args) {
        sender.sendMessage("This command isn't implemented yet.");
        // TODO: implement
        return false;
    }
}