/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.structural;

import dansapps.interakt.objects.domain.LivingEntity;

import java.util.HashSet;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Location {
    private int x;
    private int y;
    private int width;
    private int height;

    private TwoDimensionalGrid parentGrid;
    private HashSet<LivingEntity> entities = new HashSet<>();

    public Location(int x, int y, int width, int height, TwoDimensionalGrid parentGrid) {
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

    public TwoDimensionalGrid getParentGrid() {
        return parentGrid;
    }

    public void setParentGrid(TwoDimensionalGrid parentGrid) {
        this.parentGrid = parentGrid;
    }

    public HashSet<LivingEntity> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<LivingEntity> entities) {
        this.entities = entities;
    }

    public void addEntity(LivingEntity entity) {
        entities.add(entity);
    }

    public void removeEntity(LivingEntity entity) {
        entities.remove(entity);
    }

    public boolean isEntityPresent(LivingEntity entity) {
        return entities.contains(entity);
    }
}