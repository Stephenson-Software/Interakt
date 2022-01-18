/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Grid;
import preponderous.environmentlib.abs.objects.Location;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Square extends Location implements Savable {
    private UUID uuid;
    private int x;
    private int y;
    private UUID parentGridUUID;
    private HashSet<UUID> entityUUIDs = new HashSet<>();

    public Square(int x, int y, UUID gridUUID) {
        super(x, y, gridUUID);
    }

    public Square(Map<String, String> data) {
        super(-1, -1, null);
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

    public HashSet<UUID> getEntityUUIDs() {
        return entityUUIDs;
    }

    public void setEntityUUIDs(HashSet<UUID> entityUUIDs) {
        this.entityUUIDs = entityUUIDs;
    }

    public void addEntity(Actor actor) {
        entityUUIDs.add(actor.getUUID());
        actor.setLocationUUID(getUUID());
    }

    public void removeEntity(Actor actor) {
        entityUUIDs.remove(actor.getUUID());
    }

    public boolean isEntityPresent(Actor actor) {
        return entityUUIDs.contains(actor);
    }

    @Override
    public Square getRandomAdjacentLocation() {
        Random random = new Random();
        Region region = getParentGrid();
        int direction = random.nextInt(4);
        return switch (direction) {
            case 0 -> getUp(region);
            case 1 -> getRight(region);
            case 2 -> getDown(region);
            case 3 -> getLeft(region);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
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
        saveMap.put("entities", gson.toJson(entityUUIDs));
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
        entityUUIDs = gson.fromJson(data.get("entities"), hashsetTypeUUID);
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    @Override
    public Region getParentGrid() {
        try {
            return PersistentData.getInstance().getGrid(getParentGridUUID());
        } catch (Exception e) {
            Logger.getInstance().log("The parent grid of a square was not found.");
            return null;
        }
    }

    @Override
    public Square getUp(Grid grid) {
        return (Square) grid.getLocation(getX(), getY() + 1);
    }

    @Override
    public Square getRight(Grid grid) {
        return (Square) grid.getLocation(getX() + 1, getY());
    }

    @Override
    public Square getDown(Grid grid) {
        return (Square) grid.getLocation(getX(), getY() - 1);
    }

    @Override
    public Square getLeft(Grid grid) {
        return (Square) grid.getLocation(getX() - 1, getY());
    }
}