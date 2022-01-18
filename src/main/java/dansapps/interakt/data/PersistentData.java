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
    private HashSet<Actor> actors = new HashSet<>();
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

    public HashSet<Actor> getActors() {
        return actors;
    }

    public void setActors(HashSet<Actor> actors) {
        this.actors = actors;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public void removeActor(Actor actor) {
        actors.remove(actor);
    }

    public Actor getActor(String name) throws Exception {
        for (Actor actor : actors) {
            if (actor.getName().equalsIgnoreCase(name)) {
                return actor;
            }
        }
        throw new Exception();
    }

    public Actor getActor(UUID entityUUID) {
        for (Actor actor : actors) {
            if (actor.getUUID().equals(entityUUID)) {
                return actor;
            }
        }
        return null;
    }

    public HashSet<World> getWorlds() {
        return worlds;
    }

    public void setWorlds(HashSet<World> worlds) {
        this.worlds = worlds;
    }

    public void addWorld(World world) {
        worlds.add(world);
    }

    public void removeWorld(World world) {
        worlds.remove(world);
    }

    public World getWorld(String name) throws Exception {
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        throw new Exception();
    }

    public World getWorld(UUID worldUUID) {
        for (World world : worlds) {
            if (world.getUUID().equals(worldUUID)) {
                return world;
            }
        }
        return null;
    }

    public HashSet<Region> getRegions() {
        return regions;
    }

    public void setRegions(HashSet<Region> regions) {
        this.regions = regions;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public Region getRegion(UUID gridUUID) throws Exception {
        for (Region region : regions) {
            if (region.getUUID().equals(gridUUID)) {
                return region;
            }
        }
        throw new Exception();
    }

    public HashSet<Square> getSquares() {
        return squares;
    }

    public void setSquares(HashSet<Square> squares) {
        this.squares = squares;
    }

    public void addSquare(Square square) {
        squares.add(square);
    }

    public Square getSquare(UUID squareUUID) throws Exception {
        for (Square square : squares) {
            if (square.getUUID().equals(squareUUID)) {
                return square;
            }
        }
        throw new Exception();
    }

    public ArrayList<TimePartition> getTimePartitions() {
        return timePartitions;
    }

    public void setTimePartitions(ArrayList<TimePartition> timePartitions) {
        this.timePartitions = timePartitions;
    }

    public void addTimePartition(TimePartition timePartition) {
        timePartitions.add(timePartition);
    }

    public void removeTimePartition(TimePartition timePartition) {
        timePartitions.remove(timePartition);
    }

    public void clearData() {
        actors.clear();
        worlds.clear();
        regions.clear();
        squares.clear();
        timePartitions.clear();
    }
}