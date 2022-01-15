package dansapps.interakt.objects.actions;

import dansapps.interakt.objects.actions.abs.Action;
import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Location;
import dansapps.interakt.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class Move extends Action {

    public Move(String name) {
        super(name);
    }

    public void performAction(Entity entity) {
        Location currentLocation;
        try {
            currentLocation = entity.getLocation();
        } catch (Exception e) {
            return;
        }

        Location newLocation;
        try {
            newLocation = currentLocation.getRandomAdjacentLocation();
        } catch (Exception e) {
            return;
        }

        currentLocation.removeEntity(entity);
        entity.setLocationUUID(newLocation.getUUID());
        newLocation.addEntity(entity);
    }
}