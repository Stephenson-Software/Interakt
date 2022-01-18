/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class EntityFactory {
    private static EntityFactory instance;

    private EntityFactory() {

    }

    public static EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    public void createEntity(String name) {
        Actor actor = new Actor(name);
        PersistentData.getInstance().addEntity(actor);
    }
}