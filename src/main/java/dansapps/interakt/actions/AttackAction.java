/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.actions;

import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.utils.Logger;

import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 * @since January 1st, 2022
 */
public class AttackAction implements Action {

    public static void execute(Actor attacker, Actor victim) {
        if (attacker.isFriend(victim)) {
            return;
        }
        victim.setHealth(victim.getHealth() - new Random().nextInt(10)); // TODO: improve
        Event event = EventFactory.getInstance().createEvent(attacker.getName() + " has attacked " + victim.getName());
        Logger.getInstance().logEvent(event);

        ActionRecordFactory.getInstance().createActionRecord(attacker, new AttackAction());
    }

    @Override
    public String getName() {
        return "attack";
    }
}
