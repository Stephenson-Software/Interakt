package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class WipeCommand extends InteraktCommand {

    public WipeCommand() {
        super(new ArrayList<>(List.of("wipe")), new ArrayList<>(List.of("interakt.wipe")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        PersistentData.getInstance().clearData();
        sender.sendMessage("Data has been wiped.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}