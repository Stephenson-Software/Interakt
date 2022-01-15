/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class TwoDimensionalGrid implements Savable {
    private UUID uuid;
    private HashSet<UUID> locationUUIDs = new HashSet<>();
    private int columns;
    private int rows;
    private int locationHeight;
    private int locationWidth;
    private UUID primaryLocationUUID;
    private UUID parentEnvironmentUUID;

    public TwoDimensionalGrid(int columns, int rows, int locationHeight, int locationWidth, UUID parentEnvironmentUUID) {
        uuid = UUID.randomUUID();
        this.columns = columns;
        this.rows = rows;
        this.locationHeight = locationHeight;
        this.locationWidth = locationWidth;
        this.parentEnvironmentUUID = parentEnvironmentUUID;
    }

    public TwoDimensionalGrid(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
    }

    public HashSet<UUID> getLocationUUIDs() {
        return locationUUIDs;
    }

    public void setLocationUUIDs(HashSet<UUID> gridLocations) {
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

    public int getLocationHeight() {
        return locationHeight;
    }

    public void setLocationHeight(int locationHeight) {
        this.locationHeight = locationHeight;
    }

    public int getLocationWidth() {
        return locationWidth;
    }

    public void setLocationWidth(int locationWidth) {
        this.locationWidth = locationWidth;
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

    public void createGrid() {
        int xPosition = 0;
        int yPosition = 0;
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                Location newGridLocation = new Location(xPosition, yPosition, locationWidth, locationHeight, this);
                addLocation(newGridLocation);
                if (i == 0 && j == 0) {
                    primaryLocationUUID = newGridLocation.getUUID();
                }
                xPosition += locationWidth;
            }
            yPosition += locationHeight;
            xPosition = 0;
        }
    }

    public void addLocation(Location gridLocation) {
        // TODO: ensure that no locations are added with the same x and y
        locationUUIDs.add(gridLocation.getUUID());
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("locationUUIDs", gson.toJson(locationUUIDs));
        saveMap.put("columns", gson.toJson(columns));
        saveMap.put("rows", gson.toJson(rows));
        saveMap.put("locationHeight", gson.toJson(locationHeight));
        saveMap.put("locationWidth", gson.toJson(locationWidth));
        saveMap.put("primaryLocationUUID", gson.toJson(primaryLocationUUID));
        saveMap.put("parentEnvironmentUUID", gson.toJson(parentEnvironmentUUID));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        locationUUIDs = gson.fromJson(data.get("locationUUIDs"), hashsetTypeUUID);
        columns = Integer.parseInt(data.get("columns"));
        rows = Integer.parseInt(data.get("rows"));
        locationHeight = Integer.parseInt(data.get("locationHeight"));
        locationWidth = Integer.parseInt(data.get("locationWidth"));
        primaryLocationUUID = UUID.fromString(gson.fromJson(data.get("primaryLocationUUID"), String.class));
        parentEnvironmentUUID = UUID.fromString(gson.fromJson(data.get("parentEnvironmentUUID"), String.class));
    }
}