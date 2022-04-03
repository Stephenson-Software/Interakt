/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;

import java.util.Map;
import java.util.Random;

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

    public Actor createActorWithRandomName(String name) {
        if (isNameTaken(name)) {
            return null;
        }
        Actor actor = new Actor(name);
        PersistentData.getInstance().addActor(actor);
        return actor;
    }

    public Actor createActorFromParents(Actor parent1, Actor parent2) {
        Actor child = createActorWithRandomName();
        child.addParent(parent1.getUUID());
        child.addParent(parent2.getUUID());
        parent1.addChild(child.getUUID());
        parent2.addChild(child.getUUID());
        return child;
    }

    public Actor createActorWithRandomName() {
        String name;
        do {
            name = generateRandomString(5);
        } while (isNameTaken(name));
        return createActorWithRandomName(name);
    }

    public void createActorWithRandomName(Map<String, String> data) {
        Actor actor = new Actor(data);
        PersistentData.getInstance().addActor(actor);
    }

    private boolean isNameTaken(String name) {
        return PersistentData.getInstance().isActor(name);
    }

    private String generateRandomString(int length) {
        final char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new Random();
        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < length; i++) {
            toReturn.append(alphabetArray[random.nextInt(alphabetArray.length)]);
        }
        return toReturn.toString();
    }
}