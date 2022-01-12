/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.data;

import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;

import java.util.HashSet;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class PersistentData {
    private static PersistentData instance;
    private HashSet<Environment> environments = new HashSet<>();
    private HashSet<Entity> entities = new HashSet<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public HashSet<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(HashSet<Environment> environments) {
        this.environments = environments;
    }

    public HashSet<Entity> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<Entity> entities) {
        this.entities = entities;
    }

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void removeEnvironment(Environment environment) {
        environments.remove(environment);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {

    }

    public Entity getEntity(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

    public Environment getEnvironment(String name) {
        for (Environment environment : environments) {
            if (environment.getName().equalsIgnoreCase(name)) {
                return environment;
            }
        }
        return null;
    }
}