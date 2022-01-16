/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.LocationFactory;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Grid implements Savable {
    private UUID uuid;
    private ArrayList<UUID> locationUUIDs = new ArrayList<>();
    private int columns;
    private int rows;
    private UUID primaryLocationUUID;
    private UUID parentEnvironmentUUID;

    public Grid(int columns, int rows, UUID parentEnvironmentUUID) {
        uuid = UUID.randomUUID();
        this.columns = columns;
        this.rows = rows;
        createGrid();
        this.primaryLocationUUID = getFirstLocationUUID();
        this.parentEnvironmentUUID = parentEnvironmentUUID;
    }

    public Grid(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
    }

    public ArrayList<UUID> getLocationUUIDs() {
        return locationUUIDs;
    }

    public void setLocationUUIDs(ArrayList<UUID> gridLocations) {
        this.locationUUIDs = gridLocations;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public UUID getPrimaryLocationUUID() {
        return primaryLocationUUID;
    }

    public void setPrimaryLocationUUID(UUID primaryLocationUUID) {
        this.primaryLocationUUID = primaryLocationUUID;
    }

    public UUID getParentEnvironmentUUID() {
        return parentEnvironmentUUID;
    }

    public void setParentEnvironmentUUID(UUID parentEnvironmentUUID) {
        this.parentEnvironmentUUID = parentEnvironmentUUID;
    }

    public Location getLocation(int x, int y) throws Exception {
        for (UUID locationUUID : getLocationUUIDs()) {
            Location location = PersistentData.getInstance().getLocation(locationUUID);
            if (location.getX() == x && location.getY() == y) {
                return location;
            }
        }
        throw new Exception();
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("locationUUIDs", gson.toJson(locationUUIDs));
        saveMap.put("columns", gson.toJson(columns));
        saveMap.put("rows", gson.toJson(rows));
        saveMap.put("primaryLocationUUID", gson.toJson(primaryLocationUUID));
        saveMap.put("parentEnvironmentUUID", gson.toJson(parentEnvironmentUUID));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type arrayListTypeUUID = new TypeToken<ArrayList<UUID>>(){}.getType();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        locationUUIDs = gson.fromJson(data.get("locationUUIDs"), arrayListTypeUUID);
        columns = Integer.parseInt(data.get("columns"));
        rows = Integer.parseInt(data.get("rows"));
        primaryLocationUUID = UUID.fromString(gson.fromJson(data.get("primaryLocationUUID"), String.class));
        parentEnvironmentUUID = UUID.fromString(gson.fromJson(data.get("parentEnvironmentUUID"), String.class));
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                Location location;
                try {
                    location = getLocation(x, y);
                } catch (Exception e) {
                    toReturn.append("[N/A] ");
                    continue;
                }
                if (location.getEntityUUIDs().size() > 0) {
                    UUID entityUUID = location.getEntityUUIDs().iterator().next();
                    Entity entity = PersistentData.getInstance().getEntity(entityUUID);
                    toReturn.append("[").append(entity.getName().charAt(0)).append("] ");
                }
                else {
                    toReturn.append("[ ] ");
                }
            }
            toReturn.append("\n");
        }
        return toReturn.toString();
    }

    private void createGrid() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                UUID locationUUID = LocationFactory.getInstance().createLocation(i, j, getUUID());
                locationUUIDs.add(locationUUID);
            }
        }
    }

    private UUID getFirstLocationUUID() {
        return locationUUIDs.get(0);
    }

}