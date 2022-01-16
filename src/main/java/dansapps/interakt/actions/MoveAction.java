package dansapps.interakt.actions;

import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Location;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction {

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
        } catch (Exception e) {
            Logger.getInstance().log(entity.getName() + " wanted to move, but the location they wanted to move to didn't exist.");
            return;
        }
        currentLocation.removeEntity(entity);
        entity.setLocationUUID(newLocation.getUUID());
        newLocation.addEntity(entity);

        Logger.getInstance().log(entity.getName() + " moved to " + newLocation.getX() + ", " + newLocation.getY() + " in " + entity.getEnvironment().getName());
    }
}