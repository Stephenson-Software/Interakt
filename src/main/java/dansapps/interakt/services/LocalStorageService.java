/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.*;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalStorageService {
    private static LocalStorageService instance;
    private final static String FILE_PATH = "/Interakt/";
    private final static String ENTITIES_FILE_NAME = "entities.json";
    private final static String ENVIRONMENTS_FILE_NAME = "environments.json";
    private final static String TWO_DIMENSIONAL_GRIDS_FILE_NAME = "twoDimensionalGrids.json";
    private final static String LOCATIONS_FILE_NAME = "locations.json";
    private final static String TIME_SLOTS_FILE_NAME = "timeSlots.json";

    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();

    private LocalStorageService() {
        jsonWriterReader.initialize(FILE_PATH);
    }

    public static LocalStorageService getInstance() {
        if (instance == null) {
            instance = new LocalStorageService();
        }
        return instance;
    }

    public void save() {
        try {
            saveEntities();
            saveEnvironments();
            saveGrids();
            saveLocations();
            saveTimeSlots();
        }
        catch(Exception e) {
            Logger.getInstance().log("Something went wrong when saving the data of the application.");
        }
    }

    public void load() {
        try {
            loadEntities();
            loadEnvironments();
            loadGrids();
            loadLocations();
            loadTimeSlots();
        }
        catch(Exception e) {
            Logger.getInstance().log("Something went wrong when loading the data of the application.");
        }
    }

    private void saveEntities() {
        List<Map<String, String>> entities = new ArrayList<>();
        for (Actor actor : PersistentData.getInstance().getActors()){
            entities.add(actor.save());
        }
        jsonWriterReader.writeOutFiles(entities, ENTITIES_FILE_NAME);
    }

    private void saveEnvironments() {
        List<Map<String, String>> environments = new ArrayList<>();
        for (World world : PersistentData.getInstance().getWorlds()){
            environments.add(world.save());
        }
        jsonWriterReader.writeOutFiles(environments, ENVIRONMENTS_FILE_NAME);
    }

    private void saveGrids() {
        List<Map<String, String>> twoDimensionalGrids = new ArrayList<>();
        for (Region region : PersistentData.getInstance().getRegions()){
            twoDimensionalGrids.add(region.save());
        }
        jsonWriterReader.writeOutFiles(twoDimensionalGrids, TWO_DIMENSIONAL_GRIDS_FILE_NAME);
    }

    private void saveLocations() {
        List<Map<String, String>> locations = new ArrayList<>();
        for (Square square : PersistentData.getInstance().getSquares()){
            locations.add(square.save());
        }
        jsonWriterReader.writeOutFiles(locations, LOCATIONS_FILE_NAME);
    }

    private void saveTimeSlots() {
        List<Map<String, String>> timeSlots = new ArrayList<>();
        for (TimePartition timePartition : PersistentData.getInstance().getTimePartitions()){
            timeSlots.add(timePartition.save());
        }
        jsonWriterReader.writeOutFiles(timeSlots, TIME_SLOTS_FILE_NAME);
    }

    private void loadEntities() {
        PersistentData.getInstance().getActors().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ENTITIES_FILE_NAME);
        HashSet<Actor> entities = new HashSet<>();
        for (Map<String, String> entityData : data){
            Actor actor = new Actor(entityData);
            entities.add(actor);
        }
        PersistentData.getInstance().setActors(entities);
    }

    private void loadEnvironments() {
        PersistentData.getInstance().getWorlds().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ENVIRONMENTS_FILE_NAME);
        HashSet<World> worlds = new HashSet<>();
        for (Map<String, String> environmentData : data){
            World world = new World(environmentData);
            worlds.add(world);
        }
        PersistentData.getInstance().setWorlds(worlds);
    }

    private void loadGrids() {
        PersistentData.getInstance().getRegions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + TWO_DIMENSIONAL_GRIDS_FILE_NAME);
        HashSet<Region> regions = new HashSet<>();
        for (Map<String, String> twoDimensionalGridData : data){
            Region region = new Region(twoDimensionalGridData);
            regions.add(region);
        }
        PersistentData.getInstance().setRegions(regions);
    }

    private void loadLocations() {
        PersistentData.getInstance().getSquares().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + LOCATIONS_FILE_NAME);
        HashSet<Square> squares = new HashSet<>();
        for (Map<String, String> locationData : data){
            Square square = new Square(locationData);
            squares.add(square);
        }
        PersistentData.getInstance().setSquares(squares);
    }

    private void loadTimeSlots() {
        PersistentData.getInstance().getTimePartitions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + TIME_SLOTS_FILE_NAME);
        ArrayList<TimePartition> timePartitions = new ArrayList<>();
        for (Map<String, String> timeSlotData : data){
            TimePartition timePartition = new TimePartition(timeSlotData);
            timePartitions.add(timePartition);
        }
        PersistentData.getInstance().setTimePartitions(timePartitions);
    }
}