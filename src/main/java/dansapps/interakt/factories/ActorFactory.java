/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;

import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class ActorFactory {
    private static ActorFactory instance;

    private ActorFactory() {

    }

    public static ActorFactory getInstance() {
        if (instance == null) {
            instance = new ActorFactory();
        }
        return instance;
    }

    public void createActor(String name) {
        Actor actor = new Actor(name);
        PersistentData.getInstance().addActor(actor);
    }

    public void createActor(Map<String, String> data) {
        Actor actor = new Actor(data);
        PersistentData.getInstance().addActor(actor);
    }
}