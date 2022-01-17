/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.data;

import dansapps.interakt.actions.abs.Action;
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
    private HashSet<Entity> entities = new HashSet<>();
    private HashSet<Environment> environments = new HashSet<>();
    private HashSet<Grid> grids = new HashSet<>();
    private HashSet<Location> locations = new HashSet<>();
    private ArrayList<TimeSlot> timeSlots = new ArrayList<>();
    private ArrayList<ActionRecord> actionRecords = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
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

    public void deleteEntity(String name, CommandSender sender) {
        Entity entity;
        try {
            entity = PersistentData.getInstance().getEntity(name);
        } catch (Exception e) {
            sender.sendMessage("That entity wasn't found.");
            return;
        }

        performHousekeepingForEnvironment(entity);
        performHousekeepingForLocation(entity);

        PersistentData.getInstance().removeEntity(entity);
        sender.sendMessage("Entity removed.");
    }

    public Entity getEntity(String name) throws Exception {
        for (Entity entity : entities) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        throw new Exception();
    }

    public Entity getEntity(UUID entityUUID) {
        for (Entity entity : entities) {
            if (entity.getUUID().equals(entityUUID)) {
                return entity;
            }
        }
        return null;
    }

    public HashSet<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(HashSet<Environment> environments) {
        this.environments = environments;
    }

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void deleteEnvironment(String name, CommandSender sender) {
        Environment environment;
        try {
            environment = getEnvironment(name);
        } catch (Exception e) {
            sender.sendMessage("That environment wasn't found.");
            return;
        }

        // TODO: remove environment and locations from any entities

        removeEnvironment(environment);
        sender.sendMessage("Environment removed.");
    }

    public Environment getEnvironment(String name) throws Exception {
        for (Environment environment : environments) {
            if (environment.getName().equalsIgnoreCase(name)) {
                return environment;
            }
        }
        throw new Exception();
    }

    public Environment getEnvironment(UUID environmentUUID) throws Exception {
        for (Environment environment : environments) {
            if (environment.getUUID().equals(environmentUUID)) {
                return environment;
            }
        }
        throw new Exception();
    }

    public HashSet<Grid> getGrids() {
        return grids;
    }

    public void setGrids(HashSet<Grid> grids) {
        this.grids = grids;
    }

    public void addGrid(Grid grid) {
        grids.add(grid);
    }

    public Grid getGrid(UUID gridUUID) throws Exception {
        for (Grid grid : grids) {
            if (grid.getUUID().equals(gridUUID)) {
                return grid;
            }
        }
        throw new Exception();
    }

    public HashSet<Location> getLocations() {
        return locations;
    }

    public void setLocations(HashSet<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public Location getLocation(UUID locationUUID) throws Exception {
        for (Location location : locations) {
            if (location.getUUID().equals(locationUUID)) {
                return location;
            }
        }
        throw new Exception();
    }

    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }

    public ArrayList<ActionRecord> getActionRecords() {
        return actionRecords;
    }

    public void setActionRecords(ArrayList<ActionRecord> actionRecords) {
        this.actionRecords = actionRecords;
    }

    public void addActionRecord(ActionRecord actionRecord) {
        actionRecords.add(actionRecord);
    }

    public void clearData() {
        entities.clear();
        environments.clear();
        grids.clear();
        locations.clear();
        timeSlots.clear();
    }

    private void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    private void removeEnvironment(Environment environment) {
        environments.remove(environment);
    }

    private void performHousekeepingForLocation(Entity entity) {
        Location location;
        try {
            location = entity.getLocation();
        }
        catch (Exception e) {
            return;
        }
        location.removeEntity(entity);
    }

    private void performHousekeepingForEnvironment(Entity entity) {
        Environment environment;
        try {
            environment = entity.getEnvironment();
        }
        catch (Exception e) {
            return;
        }
        environment.removeEntity(entity);
    }
}