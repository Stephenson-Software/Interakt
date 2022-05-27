/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.actions;

import dansapps.interakt.Interakt;
import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.misc.enums.ACTION_TYPE;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.utils.Logger;

import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class AttackAction implements Action {
    private EventFactory eventFactory;
    private Logger logger;
    private Interakt interakt;
    private ActionRecordFactory actionRecordFactory;

    public AttackAction(EventFactory eventFactory, Logger logger, Interakt interakt, ActionRecordFactory actionRecordFactory) {
        this.eventFactory = eventFactory;
        this.logger = logger;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
    }

    public void execute(Actor attacker, Actor victim) {
        if (attacker.isFriend(victim)) {
            return;
        }

        int damage = new Random().nextInt((int) CONFIG.MAX_DAMAGE);
        victim.setHealth(victim.getHealth() - damage);

        Event event = eventFactory.createEvent(attacker.getName() + " has attacked " + victim.getName() + " and dealt " + damage + " damage.");
        logger.logEvent(event);

        if (attacker.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You have attacked " + victim.getName() + " and dealt " + damage + " damage.");
        }

        if (victim.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(attacker.getName() + " attacked you and and dealt " + damage + " damage.");
        }

        actionRecordFactory.createActionRecord(attacker, ACTION_TYPE.ATTACK);

        checkForDeath(victim);

        victim.decreaseRelation(attacker, (int) (damage * 0.10));
    }

    private void checkForDeath(Actor victim) {
        if (victim.isDead()) {
            Event event = new Event(victim.getName() + " has died.");
            logger.logEvent(event);

            if (victim.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You have died. Type 'quit' to quit the application.");
            }
        }
    }

    @Override
    public String getName() {
        return "attack";
    }
}
