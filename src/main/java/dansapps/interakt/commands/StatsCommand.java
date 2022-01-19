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
        sender.sendMessage("Number of actors: " + PersistentData.getInstance().getActors().size());
        sender.sendMessage("Number of worlds: " + PersistentData.getInstance().getWorlds().size());
        sender.sendMessage("Number of regions: " + PersistentData.getInstance().getRegions().size());
        sender.sendMessage("Number of squares: " + PersistentData.getInstance().getSquares().size());
        sender.sendMessage("Number of elapsed time partitions: " + PersistentData.getInstance().getTimePartitions().size());
        sender.sendMessage("Number of action records: " + PersistentData.getInstance().getActionRecords().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}