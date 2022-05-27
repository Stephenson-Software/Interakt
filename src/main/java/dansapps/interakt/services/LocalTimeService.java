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

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalTimeService extends TimeService {
    private Interakt interakt;
    private TimePartitionFactory timePartitionFactory;
    private Logger logger;

    public LocalTimeService(Interakt interakt, TimePartitionFactory timePartitionFactory, Logger logger) {
        this.interakt = interakt;
        this.timePartitionFactory = timePartitionFactory;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (interakt.isRunning()) {
            elapse();
            try {
                TimeUnit.SECONDS.sleep(CONFIG.TIME_SLOT_LENGTH_IN_SECONDS);
            } catch (Exception e) {
                logger.logError("Time stream was interrupted.");
            }
        }
    }

    public void elapse() {
        logger.logInfo("----------------------");
        int TIME_SLOT_LENGTH_IN_MILLISECONDS = CONFIG.TIME_SLOT_LENGTH_IN_SECONDS * 1000;
        timePartitionFactory.createTimePartition(TIME_SLOT_LENGTH_IN_MILLISECONDS);
        handleEntityActions();
        logger.logInfo("Time elapsed. Number of elapsed slots: " + PersistentData.getInstance().getTimePartitions().size());
    }

    private void handleEntityActions() {
        for (Actor actor : PersistentData.getInstance().getActors()) {
            if (actor.isDead()) {
                continue;
            }
            int choice = new Random().nextInt(4); // get random number from 0 to 3
            switch(choice) {
                case 0 -> actor.attemptToMove();
                case 1 -> actor.attemptToBefriend();
                case 2 -> actor.attemptToAttack();
                case 3 -> actor.attemptToPerformReproduceAction();
            }
        }
        PersistentData.getInstance().removeDeadActors();
    }
}