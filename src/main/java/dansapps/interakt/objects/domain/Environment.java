package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.AbstractEntity;
import dansapps.interakt.objects.structural.Grid;

import java.util.HashSet;

public class Environment extends AbstractEntity {
    private Grid grid;
    private HashSet<Entity> entities = new HashSet<>();

    public Environment(int ID, String name, int size) {
        super(ID, name);
        grid = new Grid(size, size, 10, 10);
    }

    public Grid getGrid() {
        return grid;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public boolean isEntityPresent(Entity entity) {
        return entities.contains(entity);
    }
}