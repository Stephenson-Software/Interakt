/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.data;

import dansapps.interakt.objects.*;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
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
    private LinkedList<ActionRecord> actionRecords = new LinkedList<>();

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
        removeReferencesToActor(actor);
        actors.remove(actor);
    }

    private void removeReferencesToActor(Actor actor) {
        removeActorReferencesFromEnvironments(actor);
        removeActorReferencesFromSquares(actor);
        removeActorReferencesFromActors(actor);
    }

    private void removeActorReferencesFromEnvironments(Actor actor) {
        for (World world : worlds) {
            if (world.isEntityPresent(actor)) {
                world.removeEntity(actor);
            }
        }
    }

    private void removeActorReferencesFromSquares(Actor actor) {
        for (Square square : squares) {
            if (square.isEntityPresent(actor)) {
                square.removeActor(actor);
            }
        }
    }

    private void removeActorReferencesFromActors(Actor targetActor) {
        for (Actor actor : actors) {
            if (actor.isFriend(targetActor)) {
                actor.removeFriend(targetActor);
            }
        }
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

    public LinkedList<ActionRecord> getActionRecords() {
        return actionRecords;
    }

    public void setActionRecords(LinkedList<ActionRecord> actionRecords) {
        this.actionRecords = actionRecords;
    }

    public void addActionRecord(ActionRecord actionRecord) {
        actionRecords.add(actionRecord);
    }

    public void clearData() {
        actors.clear();
        worlds.clear();
        regions.clear();
        squares.clear();
        timePartitions.clear();
        actionRecords.clear();
    }

    public boolean isWorld(String name) {
        try {
            getWorld(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isActor(String name) {
        try {
            getActor(name);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean placeIntoEnvironment(World world, CommandSender sender, Actor actor) {
        boolean success = placeIntoEnvironment(world, actor);

        if (!success) {
            sender.sendMessage("A problem occurred during placement.");
        }
        else {
            sender.sendMessage(actor.getName() + " was placed in the " + world.getName() + " world.");
        }
        return success;
    }

    public boolean placeIntoEnvironment(World world, Actor actor) {
        Square square = world.getRandomSquare();

        if (square == null) {
            return false;
        }

        if (actor.getWorld() != null) {
            return false;
        }

        world.addEntity(actor);
        square.addActor(actor);
        return true;
    }

    public Actor getActorWithMostActionRecords() {
        Actor toReturn = null;
        int max = 0;
        for (Actor actor : actors) {
            int numRecords = actor.getNumActionRecords();
            if (numRecords > max) {
                max = numRecords;
                toReturn = actor;
            }
        }
        if (toReturn == null) {
            throw new NullPointerException();
        }
        return toReturn;
    }

    public Actor getActorWithLeastActionRecords() {
        Actor toReturn = null;
        int min = 9999;
        for (Actor actor : actors) {
            int numRecords = actor.getNumActionRecords();
            if (numRecords < min) {
                min = numRecords;
                toReturn = actor;
            }
        }
        if (toReturn == null) {
            throw new NullPointerException();
        }
        return toReturn;
    }

    public Actor getMostWellTravelledActor() {
        Actor toReturn = null;
        int max = 0;
        for (Actor actor : actors) {
            int numExploredChunks = actor.getNumExploredChunks();
            if (numExploredChunks > max) {
                max = numExploredChunks;
                toReturn = actor;
            }
        }
        if (toReturn == null) {
            throw new NullPointerException();
        }
        return toReturn;
    }

    public Actor getMostFriendlyActor() {
        Actor toReturn = null;
        int max = 0;
        for (Actor actor : actors) {
            int numFriends = actor.getNumFriends();
            if (numFriends > max) {
                max = numFriends;
                toReturn = actor;
            }
        }
        if (toReturn == null) {
            throw new NullPointerException();
        }
        return toReturn;
    }

    public void removeDeadActors() {
        ArrayList<Actor> toRemove = new ArrayList<>();
        for (Actor actor : getActors()) {
            if (actor.isDead()) {
                toRemove.add(actor);
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            Actor actor = toRemove.get(i);
            removeActor(actor);
        }
    }

    public int getSecondsElapsed() {
        return getMillisecondsElapsed() / 1000;
    }

    private int getMillisecondsElapsed() {
        int sum = 0;
        for (TimePartition timePartition : timePartitions) {
            sum += timePartition.getMilliseconds();
        }
        return sum;
    }

    public int getNumActors() {
        return actors.size();
    }

    public int getNumWorlds() {
        return worlds.size();
    }
}