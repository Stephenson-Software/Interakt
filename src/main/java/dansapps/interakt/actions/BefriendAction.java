/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.actions;

import dansapps.interakt.Interakt;
import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.misc.enums.ACTION_TYPE;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.utils.Logger;

import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 * @since January 22nd, 2022
 */
public class BefriendAction implements Action {
    private EventFactory eventFactory;
    private Logger logger;
    private Interakt interakt;
    private ActionRecordFactory actionRecordFactory;

    public BefriendAction(EventFactory eventFactory, Logger logger, Interakt interakt, ActionRecordFactory actionRecordFactory) {
        this.eventFactory = eventFactory;
        this.logger = logger;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
    }

    public void execute(Actor actor, Actor other) {
        Event event = eventFactory.createEvent(actor.getName() + " was friendly to " + other.getName());
        logger.logEvent(event);

        if (actor.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You were friendly to " + other.getName() + ".");
        }

        if (other.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(actor.getName() + " was friendly to you.");
        }

        actionRecordFactory.createActionRecord(actor, ACTION_TYPE.BEFRIEND);

        actor.increaseRelation(other, new Random().nextInt(10));
        other.increaseRelation(actor, new Random().nextInt(10));
    }

    @Override
    public String getName() {
        return "befriend";
    }
}
