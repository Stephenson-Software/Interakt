/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;


import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.utils.Logger;

import java.util.Map;
import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class ActorFactory {
    private final EntityRecordFactory entityRecordFactory;
    private final Logger logger;
    private final EventFactory eventFactory;
    private final Interakt interakt;
    private final ActionRecordFactory actionRecordFactory;
    private final PersistentData persistentData;
    private final FoodItemFactory foodItemFactory;

    public ActorFactory(EntityRecordFactory entityRecordFactory, Logger logger, EventFactory eventFactory, Interakt interakt, ActionRecordFactory actionRecordFactory, PersistentData persistentData, FoodItemFactory foodItemFactory) {
        this.entityRecordFactory = entityRecordFactory;
        this.logger = logger;
        this.eventFactory = eventFactory;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
        this.persistentData = persistentData;
        this.foodItemFactory = foodItemFactory;
    }

    public Actor createActorWithName(String name) throws NameTakenException {
        if (isNameTaken(name)) {
            throw new NameTakenException();
        }
        Actor actor = new Actor(name, logger, eventFactory, interakt, actionRecordFactory, this, persistentData, foodItemFactory);
        persistentData.addActor(actor);
        entityRecordFactory.createEntityRecord(actor);
        return actor;
    }

    public Actor createActorFromParents(Actor parent1, Actor parent2) {
        Actor child = createActorWithRandomName();

        // genealogy
        child.addParent(parent1.getUUID());
        child.addParent(parent2.getUUID());
        parent1.addChild(child.getUUID());
        parent2.addChild(child.getUUID());

        // relations
        child.setRelation(parent1, 100);
        child.setRelation(parent2, 100);
        parent1.setRelation(child, 100);
        parent2.setRelation(child, 100);

        return child;
    }

    public Actor createActorWithRandomName() {
        String name;
        do {
            name = generateRandomString(5);
        } while (isNameTaken(name));
        try {
            return createActorWithName(name);
        } catch (NameTakenException e) {
            return createActorWithRandomName();
        }
    }

    public void createActorWithData(Map<String, String> data) {
        Actor actor = new Actor(data, logger, eventFactory, interakt, actionRecordFactory, this, persistentData, foodItemFactory);
        persistentData.addActor(actor);
        entityRecordFactory.createEntityRecord(actor);

    }

    private boolean isNameTaken(String name) {
        return persistentData.isActor(name);
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