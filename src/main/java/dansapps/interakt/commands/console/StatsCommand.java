package dansapps.interakt.commands.console;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ZeroFriendshipsExistentException;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StatsCommand extends InteraktCommand {
    private final Logger logger;
    private final PersistentData persistentData;

    public StatsCommand(Logger logger, PersistentData persistentData) {
        super(new ArrayList<>(List.of("stats")), new ArrayList<>(List.of("interakt.stats")));
        this.logger = logger;
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        try {
            sender.sendMessage("Number of actors: " + persistentData.getActors().size());
            sender.sendMessage("Number of worlds: " + persistentData.getWorlds().size());
            sender.sendMessage("Number of regions: " + persistentData.getRegions().size());
            sender.sendMessage("Number of squares: " + persistentData.getSquares().size());
            sender.sendMessage("Number of elapsed time partitions: " + persistentData.getTimePartitions().size());
            sender.sendMessage("Number of action records: " + persistentData.getActionRecords().size());
            sender.sendMessage("Number of entity records: " + persistentData.getEntityRecords().size());
            sender.sendMessage("Most active actor: " + getNameOrNotApplicable(persistentData::getActorWithMostActionRecords));
            sender.sendMessage("Least active actor: " + getNameOrNotApplicable(persistentData::getActorWithLeastActionRecords));
            sender.sendMessage("Most well travelled: " + getNameOrNotApplicable(persistentData::getMostWellTravelledActor));

            try {
                sender.sendMessage("Most friendly actor: " + persistentData.getMostFriendlyActor().getName());
            } catch(ZeroFriendshipsExistentException e) {
                sender.sendMessage("Most friendly actor: N/A");
            }

            sender.sendMessage("Minutes elapsed: " + persistentData.getSecondsElapsed()/60);
            return true;
        }
        catch (Exception e) {
            logger.logError("Something went wrong when printing stats.");
            return false;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        return execute(sender);
    }

    /**
     * The PersistentData actor lookups throw a NullPointerException when no actor qualifies,
     * e.g. when no actor has acted or explored yet. Report that as N/A rather than failing.
     */
    private String getNameOrNotApplicable(Supplier<Actor> lookup) {
        try {
            return lookup.get().getName();
        } catch (NullPointerException e) {
            return "N/A";
        }
    }
}