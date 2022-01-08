package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class DeleteCommand extends AbstractCommand {

    public DeleteCommand() {
        super(new ArrayList<>(List.of("delete")), new ArrayList<>(List.of("interakt.delete")));
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