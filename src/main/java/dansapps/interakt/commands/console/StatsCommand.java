package dansapps.interakt.commands.console;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand extends InteraktCommand {

    public StatsCommand() {
        super(new ArrayList<>(List.of("stats")), new ArrayList<>(List.of("interakt.stats")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        try {
            sender.sendMessage("Number of actors: " + PersistentData.getInstance().getActors().size());
            sender.sendMessage("Number of worlds: " + PersistentData.getInstance().getWorlds().size());
            sender.sendMessage("Number of regions: " + PersistentData.getInstance().getRegions().size());
            sender.sendMessage("Number of squares: " + PersistentData.getInstance().getSquares().size());
            sender.sendMessage("Number of elapsed time partitions: " + PersistentData.getInstance().getTimePartitions().size());
            sender.sendMessage("Number of action records: " + PersistentData.getInstance().getActionRecords().size());
            sender.sendMessage("Most active actor: " + PersistentData.getInstance().getActorWithMostActionRecords().getName());
            sender.sendMessage("Least active actor: " + PersistentData.getInstance().getActorWithLeastActionRecords().getName());
            sender.sendMessage("Most well travelled: " + PersistentData.getInstance().getMostWellTravelledActor().getName());
            sender.sendMessage("Most friendly actor: " + PersistentData.getInstance().getMostFriendlyActor().getName());
            sender.sendMessage("Minutes elapsed: " + PersistentData.getInstance().getSecondsElapsed()/60);
            return true;
        }
        catch (Exception e) {
            Logger.getInstance().logError("Something went wrong when printing stats.");
            return false;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }
}