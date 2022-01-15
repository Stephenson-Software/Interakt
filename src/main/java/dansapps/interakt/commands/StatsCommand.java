package dansapps.interakt.commands;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand extends InteraktCommand {

    public StatsCommand() {
        super(new ArrayList<>(List.of("stats")), new ArrayList<>(List.of("interakt.stats")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Number of entities: " + PersistentData.getInstance().getEntities().size());
        sender.sendMessage("Number of environments: " + PersistentData.getInstance().getEnvironments().size());
        sender.sendMessage("Number of grids: " + PersistentData.getInstance().getGrids().size());
        sender.sendMessage("Number of locations: " + PersistentData.getInstance().getLocations().size());
        sender.sendMessage("Number of time slots: " + PersistentData.getInstance().getTimeSlots().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}