/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.World;
import dansapps.interakt.utils.Logger;

import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class WorldFactory {
    private RegionFactory regionFactory;
    private Logger logger;
    private final PersistentData persistentData;

    public WorldFactory(RegionFactory regionFactory, Logger logger, PersistentData persistentData) {
        this.regionFactory = regionFactory;
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public void createWorld(String name) {
        if (isNameTaken(name)) {
            return;
        }
        World world = new World(name, regionFactory, logger, persistentData);
        persistentData.addWorld(world);
    }

    public void createWorld(Map<String, String> data) {
        World world = new World(data, logger, persistentData);
        persistentData.addWorld(world);
    }

    private boolean isNameTaken(String name) {
        return persistentData.isWorld(name);
    }
}