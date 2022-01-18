/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.data;

import dansapps.interakt.objects.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class PersistentData {
    private static PersistentData instance;
    private HashSet<Actor> entities = new HashSet<>();
    private HashSet<World> worlds = new HashSet<>();
    private HashSet<Region> regions = new HashSet<>();
    private HashSet<Square> squares = new HashSet<>();
    private ArrayList<TimePartition> timePartitions = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public HashSet<Actor> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<Actor> entities) {
        this.entities = entities;
    }

    public void addEntity(Actor actor) {
        entities.add(actor);
    }

    public void removeEntity(Actor actor) {
        entities.remove(actor);
    }

    public Actor getEntity(String name) throws Exception {
        for (Actor actor : entities) {
            if (actor.getName().equalsIgnoreCase(name)) {
                return actor;
            }
        }
        throw new Exception();
    }

    public Actor getEntity(UUID entityUUID) {
        for (Actor actor : entities) {
            if (actor.getUUID().equals(entityUUID)) {
                return actor;
            }
        }
        return null;
    }

    public HashSet<World> getEnvironments() {
        return worlds;
    }

    public void setEnvironments(HashSet<World> worlds) {
        this.worlds = worlds;
    }

    public void addEnvironment(World world) {
        worlds.add(world);
    }

    public void removeEnvironment(World world) {
        worlds.remove(world);
    }

    public World getEnvironment(String name) throws Exception {
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        throw new Exception();
    }

    public World getEnvironment(UUID environmentUUID) {
        for (World world : worlds) {
            if (world.getUUID().equals(environmentUUID)) {
                return world;
            }
        }
        return null;
    }

    public HashSet<Region> getGrids() {
        return regions;
    }

    public void setGrids(HashSet<Region> regions) {
        this.regions = regions;
    }

    public void addGrid(Region region) {
        regions.add(region);
    }

    public Region getGrid(UUID gridUUID) throws Exception {
        for (Region region : regions) {
            if (region.getUUID().equals(gridUUID)) {
                return region;
            }
        }
        throw new Exception();
    }

    public HashSet<Square> getLocations() {
        return squares;
    }

    public void setLocations(HashSet<Square> squares) {
        this.squares = squares;
    }

    public void addLocation(Square square) {
        squares.add(square);
    }

    public Square getLocation(UUID locationUUID) throws Exception {
        for (Square square : squares) {
            if (square.getUUID().equals(locationUUID)) {
                return square;
            }
        }
        throw new Exception();
    }

    public ArrayList<TimePartition> getTimeSlots() {
        return timePartitions;
    }

    public void setTimeSlots(ArrayList<TimePartition> timePartitions) {
        this.timePartitions = timePartitions;
    }

    public void addTimeSlot(TimePartition timePartition) {
        timePartitions.add(timePartition);
    }

    public void removeTimeSlot(TimePartition timePartition) {
        timePartitions.remove(timePartition);
    }

    public void clearData() {
        entities.clear();
        worlds.clear();
        regions.clear();
        squares.clear();
        timePartitions.clear();
    }
}