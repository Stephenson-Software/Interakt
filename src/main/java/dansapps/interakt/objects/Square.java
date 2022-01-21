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

    public Square(int x, int y, UUID gridUUID) {
        super(x, y, gridUUID);
    }

    public Square(Map<String, String> data) {
        super(-1, -1, null);
        this.load(data);
    }

    public void addActor(Actor actor) {
        addEntity(actor);
        actor.setLocationUUID(getUUID());
    }

    public void removeActor(Actor actor) {
        removeEntity(actor);
    }

    public boolean isActorPresent(Actor actor) {
        return isEntityPresent(actor);
    }

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
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("x", gson.toJson(getX()));
        saveMap.put("y", gson.toJson(getY()));
        saveMap.put("parentGridUUID", gson.toJson(getParentGridUUID()));
        saveMap.put("entities", gson.toJson(getEntityUUIDs()));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
        setX(Integer.parseInt(gson.fromJson(data.get("x"), String.class)));
        setY(Integer.parseInt(gson.fromJson(data.get("y"), String.class)));
        setParentGridUUID(UUID.fromString(gson.fromJson(data.get("parentGridUUID"), String.class)));
        setEntityUUIDs(gson.fromJson(data.get("entities"), hashsetTypeUUID));
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    public Region getParentGrid() {
        try {
            return PersistentData.getInstance().getRegion(getParentGridUUID());
        } catch (Exception e) {
            Logger.getInstance().logError("The parent grid of a square was not found.");
            return null;
        }
    }

    public Square getUp(Grid grid) {
        return (Square) grid.getLocation(getX(), getY() + 1);
    }

    public Square getRight(Grid grid) {
        return (Square) grid.getLocation(getX() + 1, getY());
    }

    public Square getDown(Grid grid) {
        return (Square) grid.getLocation(getX(), getY() - 1);
    }

    public Square getLeft(Grid grid) {
        return (Square) grid.getLocation(getX() - 1, getY());
    }
}