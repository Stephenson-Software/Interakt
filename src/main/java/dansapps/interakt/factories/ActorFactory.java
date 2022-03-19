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
    private final char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private ActorFactory() {

    }

    public static ActorFactory getInstance() {
        if (instance == null) {
            instance = new ActorFactory();
        }
        return instance;
    }

    public Actor createActor(String name) {
        if (isNameTaken(name)) {
            return null;
        }
        Actor actor = new Actor(name);
        PersistentData.getInstance().addActor(actor);
        return actor;
    }

    public Actor createActor() {
        String name;
        do {
            name = generateRandomString(5);
        } while (isNameTaken(name));
        return createActor(name);
    }

    public void createActor(Map<String, String> data) {
        Actor actor = new Actor(data);
        PersistentData.getInstance().addActor(actor);
    }

    private boolean isNameTaken(String name) {
        return PersistentData.getInstance().isActor(name);
    }

    private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < length; i++) {
            toReturn.append(alphabetArray[random.nextInt(alphabetArray.length)]);
        }
        return toReturn.toString();
    }
}