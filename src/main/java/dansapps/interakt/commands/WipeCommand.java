package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WipeCommand extends InteraktCommand {

    public WipeCommand() {
        super(new ArrayList<>(List.of("wipe")), new ArrayList<>(List.of("interakt.wipe")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        PersistentData.getInstance().clearData();
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}