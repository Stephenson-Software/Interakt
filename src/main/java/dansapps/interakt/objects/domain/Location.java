/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Location implements Savable {
    private UUID uuid;
    private int x;
    private int y;
    private UUID parentGridUUID;
    private HashSet<Entity> entities = new HashSet<>();

    public Location(int x, int y, UUID gridUUID) {
        uuid = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.parentGridUUID = gridUUID;
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

    public UUID getParentGridUUID() {
        return parentGridUUID;
    }

    public void setParentGridUUID(UUID parentGridUUID) {
        this.parentGridUUID = parentGridUUID;
    }

    public HashSet<Entity> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.setLocationUUID(getUUID());
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public boolean isEntityPresent(Entity entity) {
        return entities.contains(entity);
    }

    public Location getRandomAdjacentLocation() throws Exception {
        Random random = new Random();
        Grid grid = getParentGrid();
        int direction = random.nextInt(4);
        return switch (direction) {
            case 0 -> getUp(grid);
            case 1 -> getRight(grid);
            case 2 -> getDown(grid);
            case 3 -> getLeft(grid);
            default -> throw new Exception();
        };
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("x", gson.toJson(x));
        saveMap.put("y", gson.toJson(y));
        saveMap.put("parentGridUUID", gson.toJson(parentGridUUID));
        saveMap.put("entities", gson.toJson(entities));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        x = Integer.parseInt(gson.fromJson(data.get("x"), String.class));
        y = Integer.parseInt(gson.fromJson(data.get("y"), String.class));
        parentGridUUID = UUID.fromString(gson.fromJson(data.get("parentGridUUID"), String.class));
        entities = gson.fromJson(data.get("entities"), hashsetTypeUUID);
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    private Grid getParentGrid() throws Exception {
        return PersistentData.getInstance().getGrid(getParentGridUUID());
    }

    private Location getUp(Grid grid) throws Exception {
        return grid.getLocation(getX(), getY() + 1);
    }

    private Location getRight(Grid grid) throws Exception {
        return grid.getLocation(getX() + 1, getY());
    }

    private Location getDown(Grid grid) throws Exception {
        return grid.getLocation(getX(), getY() - 1);
    }

    private Location getLeft(Grid grid) throws Exception {
        return grid.getLocation(getX() - 1, getY());
    }
}