/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.structural;

import dansapps.interakt.objects.domain.Environment;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class TwoDimensionalGrid {
    private final UUID uuid;
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

    public UUID getUUID() {
        return uuid;
    }

    private HashSet<Location> locations = new HashSet<>();

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

    public HashSet<Location> getLocations() {
        return locations;
    }

    public void setLocations(HashSet<Location> gridLocations) {
        this.locations = gridLocations;
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
        locations.add(gridLocation);
    }
}