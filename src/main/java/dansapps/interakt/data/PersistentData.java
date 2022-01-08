package dansapps.interakt.data;

import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;
import dansapps.interakt.objects.structural.Grid;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class PersistentData {
    private static PersistentData instance;

    private HashSet<Environment> environments = new HashSet<Environment>();
    private HashSet<Entity> entities = new HashSet<Entity>();

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

    public void addEnvironment(Environment grid) {
        environments.add(grid);
    }

    public void removeEnvironment(Grid grid) {
        environments.remove(grid);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {

    }
}