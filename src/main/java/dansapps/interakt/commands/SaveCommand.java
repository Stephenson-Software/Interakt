package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.services.LocalStorageService;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class SaveCommand extends InteraktCommand {

    public SaveCommand() {
        super(new ArrayList<>(List.of("save")), new ArrayList<>(List.of("interakt.save")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        LocalStorageService.getInstance().save();
        sender.sendMessage("Saved.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}