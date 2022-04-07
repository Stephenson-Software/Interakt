/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.actions;

import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.utils.Logger;

import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class AttackAction implements Action {

    public static void execute(Actor attacker, Actor victim) {
        if (attacker.isFriend(victim)) {
            return;
        }

        int damage = new Random().nextInt((int) CONFIG.MAX_DAMAGE);
        victim.setHealth(victim.getHealth() - damage);

        Event event = EventFactory.getInstance().createEvent(attacker.getName() + " has attacked " + victim.getName() + " and dealt " + damage + " damage.");
        Logger.getInstance().logEvent(event);

        ActionRecordFactory.getInstance().createActionRecord(attacker, new AttackAction());

        checkForDeath(victim);

        victim.decreaseRelation(attacker, 10);
    }

    private static void checkForDeath(Actor victim) {
        if (victim.isDead()) {
            Event event = new Event(victim.getName() + " has died.");
            Logger.getInstance().logEvent(event);
        }
    }

    @Override
    public String getName() {
        return "attack";
    }
}
