package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Location;

import java.util.UUID;

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