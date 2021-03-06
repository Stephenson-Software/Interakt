package dansapps.interakt.commands.console;

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
    private final PersistentData persistentData;

    public WipeCommand(PersistentData persistentData) {
        super(new ArrayList<>(List.of("wipe")), new ArrayList<>(List.of("interakt.wipe")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        persistentData.clearData();
        sender.sendMessage("Data has been wiped.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}