/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;
import dansapps.interakt.objects.domain.Location;
import dansapps.interakt.objects.domain.TwoDimensionalGrid;
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
        }
        catch(Exception e) {
            Logger.getInstance().log("Something went wrong when loading the data of the application.");
        }
    }

    private void saveEntities() {
        List<Map<String, String>> links = new ArrayList<>();
        for (Entity entity : PersistentData.getInstance().getEntities()){
            links.add(entity.save());
        }
        jsonWriterReader.writeOutFiles(links, ENTITIES_FILE_NAME);
    }

    private void saveEnvironments() {
        List<Map<String, String>> environments = new ArrayList<>();
        for (Environment environment : PersistentData.getInstance().getEnvironments()){
            environments.add(environment.save());
        }
        jsonWriterReader.writeOutFiles(environments, ENVIRONMENTS_FILE_NAME);
    }

    private void saveGrids() {
        List<Map<String, String>> twoDimensionalGrids = new ArrayList<>();
        for (TwoDimensionalGrid twoDimensionalGrid : PersistentData.getInstance().getGrids()){
            twoDimensionalGrids.add(twoDimensionalGrid.save());
        }
        jsonWriterReader.writeOutFiles(twoDimensionalGrids, TWO_DIMENSIONAL_GRIDS_FILE_NAME);
    }

    private void saveLocations() {
        List<Map<String, String>> locations = new ArrayList<>();
        for (Location location : PersistentData.getInstance().getLocations()){
            locations.add(location.save());
        }
        jsonWriterReader.writeOutFiles(locations, LOCATIONS_FILE_NAME);
    }

    private void loadEntities() {
        PersistentData.getInstance().getEntities().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ENTITIES_FILE_NAME);
        HashSet<Entity> entities = new HashSet<>();
        for (Map<String, String> entityData : data){
            Entity entity = new Entity(entityData);
            entities.add(entity);
        }
        PersistentData.getInstance().setEntities(entities);
    }

    private void loadEnvironments() {
        PersistentData.getInstance().getEnvironments().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ENVIRONMENTS_FILE_NAME);
        HashSet<Environment> environments = new HashSet<>();
        for (Map<String, String> environmentData : data){
            Environment environment = new Environment(environmentData);
            environments.add(environment);
        }
        PersistentData.getInstance().setEnvironments(environments);
    }

    private void loadGrids() {
        PersistentData.getInstance().getGrids().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + TWO_DIMENSIONAL_GRIDS_FILE_NAME);
        HashSet<TwoDimensionalGrid> twoDimensionalGrids = new HashSet<>();
        for (Map<String, String> twoDimensionalGridData : data){
            TwoDimensionalGrid twoDimensionalGrid = new TwoDimensionalGrid(twoDimensionalGridData);
            twoDimensionalGrids.add(twoDimensionalGrid);
        }
        PersistentData.getInstance().setGrids(twoDimensionalGrids);
    }

    private void loadLocations() {
        PersistentData.getInstance().getLocations().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + LOCATIONS_FILE_NAME);
        HashSet<Location> twoDimensionalGrids = new HashSet<>();
        for (Map<String, String> locationData : data){
            Location location = new Location(locationData);
            twoDimensionalGrids.add(location);
        }
        PersistentData.getInstance().setLocations(twoDimensionalGrids);
    }
}