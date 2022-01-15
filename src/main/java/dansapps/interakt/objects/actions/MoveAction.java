package dansapps.interakt.objects.actions;

import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Location;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class MoveAction {

    public static void execute(Entity entity) throws Exception {
        Location currentLocation = entity.getLocation();
        Location newLocation = currentLocation.getRandomAdjacentLocation();
        currentLocation.removeEntity(entity);
        entity.setLocationUUID(newLocation.getUUID());
        newLocation.addEntity(entity);
    }
}