package dansapps.interakt.actions;

import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Square;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction {

    public static void execute(Actor actor) {
        Square currentSquare;
        try {
            currentSquare = actor.getSquare();
        } catch (Exception e) {
            Logger.getInstance().log(actor.getName() + " wanted to move, but their location wasn't found.");
            return;
        }
        Square newSquare;
        try {
            newSquare = currentSquare.getRandomAdjacentLocation();
        } catch (Exception ignored) {
            return;
        }
        currentSquare.removeActor(actor);
        actor.setLocationUUID(newSquare.getUUID());
        newSquare.addActor(actor);

        Logger.getInstance().log(actor.getName() + " moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + actor.getEnvironment().getName());
    }
}