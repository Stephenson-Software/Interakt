/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Region;
import dansapps.interakt.misc.CONFIG;

import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class GridFactory {
    private static GridFactory instance;

    private GridFactory() {

    }

    public static GridFactory getInstance() {
        if (instance == null) {
            instance = new GridFactory();
        }
        return instance;
    }

    public UUID createGrid(UUID environmentUUID) {
        Region region = new Region(CONFIG.GRID_SIZE, CONFIG.GRID_SIZE, environmentUUID);
        PersistentData.getInstance().addRegion(region);
        return region.getUUID();
    }
}
