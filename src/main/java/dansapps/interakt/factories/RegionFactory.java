/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Region;
import dansapps.interakt.utils.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class RegionFactory {
    private SquareFactory squareFactory;
    private Logger logger;

    public RegionFactory(SquareFactory squareFactory, Logger logger) {
        this.squareFactory = squareFactory;
        this.logger = logger;
    }

    public UUID createRegion(UUID environmentUUID) {
        Region region = new Region(CONFIG.GRID_SIZE, CONFIG.GRID_SIZE, environmentUUID, squareFactory, logger);
        PersistentData.getInstance().addRegion(region);
        return region.getUUID();
    }

    public void createRegion(Map<String, String> data) {
        Region region = new Region(data, logger);
        PersistentData.getInstance().addRegion(region);
    }
}