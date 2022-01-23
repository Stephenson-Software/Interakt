/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.TimePartitionFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.services.TimeService;

import java.util.concurrent.TimeUnit;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalTimeService extends TimeService {
    private static LocalTimeService instance;

    public static LocalTimeService getInstance() {
        if (instance == null) {
            instance = new LocalTimeService();
        }
        return instance;
    }

    @Override
    public void run() {
        while (Interakt.getInstance().isRunning()) {
            elapse();
            try {
                TimeUnit.SECONDS.sleep(CONFIG.TIME_SLOT_LENGTH_IN_SECONDS);
            } catch (Exception e) {
                Logger.getInstance().logError("Time stream was interrupted.");
            }
        }
    }

    public void elapse() {
        Logger.getInstance().logInfo("----------------------");
        int TIME_SLOT_LENGTH_IN_MILLISECONDS = CONFIG.TIME_SLOT_LENGTH_IN_SECONDS * 1000;
        TimePartitionFactory.getInstance().createTimePartition(TIME_SLOT_LENGTH_IN_MILLISECONDS);
        handleEntityActions();
        Logger.getInstance().logInfo("Time elapsed. Number of elapsed slots: " + PersistentData.getInstance().getTimePartitions().size());
    }

    private void handleEntityActions() {
        for (Actor actor : PersistentData.getInstance().getActors()) {
            if (actor.isDead()) {
                continue;
            }
            actor.performMoveActionIfRollSuccessful();
            actor.performBefriendActionIfActorPresentAndRollSuccessful();
            actor.performAttackActionIfActorPresentAndRollSuccessful();
        }
        PersistentData.getInstance().removeDeadActors();
    }
}