package dansapps.interakt.commands;

import preponderous.ponder.system.abs.AbstractCommand;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewCommand extends AbstractCommand {

    public ViewCommand() {
        super(new ArrayList<>(Arrays.asList("view")), new ArrayList<>(Arrays.asList("interakt.view")));
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