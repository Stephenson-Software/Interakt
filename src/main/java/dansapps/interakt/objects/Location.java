/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import preponderous.ponder.misc.abs.Savable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Location implements Savable {
    private UUID uuid;
    private int x;
    private int y;
    private int width;
    private int height;

    private TwoDimensionalGrid parentGrid;
    private HashSet<Entity> entities = new HashSet<>();

    public Location(int x, int y, int width, int height, TwoDimensionalGrid parentGrid) {
        uuid = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parentGrid = parentGrid;
    }

    public Location(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
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

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("x", gson.toJson(x));
        saveMap.put("y", gson.toJson(y));
        saveMap.put("width", gson.toJson(width));
        saveMap.put("height", gson.toJson(height));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        x = Integer.parseInt(gson.fromJson(data.get("x"), String.class));
        y = Integer.parseInt(gson.fromJson(data.get("y"), String.class));
        width = Integer.parseInt(gson.fromJson(data.get("width"), String.class));
        height = Integer.parseInt(gson.fromJson(data.get("height"), String.class));
    }
}