package dansapps.interakt.actions;

import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.objects.Entity;
import dansapps.interakt.objects.Location;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction implements Action {

    public static void execute(Entity entity) {
        Location currentLocation;
        try {
            currentLocation = entity.getLocation();
        } catch (Exception e) {
            Logger.getInstance().log(entity.getName() + " wanted to move, but their location wasn't found.");
            return;
        }
        Location newLocation;
        try {
            newLocation = currentLocation.getRandomAdjacentLocation();
        } catch (Exception ignored) {
            return;
        }
        currentLocation.removeEntity(entity);
        entity.setLocationUUID(newLocation.getUUID());
        newLocation.addEntity(entity);

        try {
            Logger.getInstance().log(entity.getName() + " moved to " + newLocation.getX() + ", " + newLocation.getY() + " in " + entity.getEnvironment().getName());
        } catch (Exception e) {
            Logger.getInstance().log(entity.getName() + " moved, but their environment wasn't found. This is likelya n er");
        }
    }
}