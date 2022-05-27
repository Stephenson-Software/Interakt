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
import dansapps.interakt.objects.Square;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction implements Action {
    private Logger logger;
    private EventFactory eventFactory;
    private Interakt interakt;
    private ActionRecordFactory actionRecordFactory;

    public MoveAction(Logger logger, EventFactory eventFactory, Interakt interakt, ActionRecordFactory actionRecordFactory) {
        this.logger = logger;
        this.eventFactory = eventFactory;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
    }

    public void execute(Actor actor) {
        Square currentSquare;
        try {
            currentSquare = actor.getSquare();
        } catch (Exception e) {
            logger.logError(actor.getName() + " wanted to move, but their location wasn't found.");
            return;
        }

        Square newSquare;
        try {
            newSquare = currentSquare.getRandomAdjacentLocation();
        } catch (Exception ignored) {
            return;
        }

        if (newSquare == null) {
            return;
        }

        currentSquare.removeActor(actor);
        actor.setLocationUUID(newSquare.getUUID());
        newSquare.addActor(actor);

        try {
            Event event = eventFactory.createEvent(actor.getName() + " moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + actor.getWorld().getName());
            logger.logEvent(event);

            if (actor.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You have moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + actor.getWorld().getName());
            }
        } catch (Exception e) {
            logger.logError(actor.getName() + " moved, but their environment wasn't found.");
        }

        actor.addSquareIfNotExplored(newSquare, eventFactory);

        actionRecordFactory.createActionRecord(actor, ACTION_TYPE.MOVE);
    }

    @Override
    public String getName() {
        return "move";
    }
}