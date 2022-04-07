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
 * @since January 22nd, 2022
 */
public class BefriendAction implements Action {

    public static void execute(Actor actor, Actor other) {
        Event event = EventFactory.getInstance().createEvent(actor.getName() + " was friendly to " + other.getName());
        Logger.getInstance().logEvent(event);

        ActionRecordFactory.getInstance().createActionRecord(actor, new BefriendAction());

        actor.increaseRelation(other, new Random().nextInt(10));
        other.increaseRelation(actor, new Random().nextInt(10));
    }

    @Override
    public String getName() {
        return "befriend";
    }
}
