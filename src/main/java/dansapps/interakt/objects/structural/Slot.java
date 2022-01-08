package dansapps.interakt.objects.structural;

import dansapps.interakt.objects.domain.Entity;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class Slot {
    private int x;
    private int y;
    private int width;
    private int height;

    private Grid parentGrid;

    private HashSet<Entity> entities = new HashSet<>();

    public Slot(int x, int y, int width, int height, Grid parentGrid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parentGrid = parentGrid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public HashSet<Entity> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<Entity> entities) {
        this.entities = entities;
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

    public Grid getParentGrid() {
        return parentGrid;
    }

    public void setParentGrid(Grid parentGrid) {
        this.parentGrid = parentGrid;
    }
}