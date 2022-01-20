/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.World;

import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class WorldFactory {
    private static WorldFactory instance;

    private WorldFactory() {

    }

    public static WorldFactory getInstance() {
        if (instance == null) {
            instance = new WorldFactory();
        }
        return instance;
    }

    public void createWorld(String name) {
        World world = new World(name);
        PersistentData.getInstance().addWorld(world);
    }

    public void createWorld(Map<String, String> data) {
        World world = new World(data);
        PersistentData.getInstance().addWorld(world);
    }
}