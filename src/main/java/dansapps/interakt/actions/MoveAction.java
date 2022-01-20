package dansapps.interakt.actions;

import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Square;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction implements Action {

    public static void execute(Actor actor) {
        Square currentSquare;
        try {
            currentSquare = actor.getSquare();
        } catch (Exception e) {
            Logger.getInstance().logError(actor.getName() + " wanted to move, but their location wasn't found.");
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
            Logger.getInstance().logInfo(actor.getName() + " moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + actor.getWorld().getName());
        } catch (Exception e) {
            Logger.getInstance().logError(actor.getName() + " moved, but their environment wasn't found.");
        }
    }
}