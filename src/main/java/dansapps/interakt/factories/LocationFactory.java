/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Location;

import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class LocationFactory {
    private static LocationFactory instance;

    private LocationFactory() {

    }

    public static LocationFactory getInstance() {
        if (instance == null) {
            instance = new LocationFactory();
        }
        return instance;
    }

    public UUID createLocation(int x, int y, UUID gridUUID) {
        Location location = new Location(x, y, gridUUID);
        PersistentData.getInstance().addLocation(location);
        return location.getUUID();
    }
}