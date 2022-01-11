/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Entity;
import dansapps.interakt.objects.domain.Environment;
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
        saveEntities();
        saveEnvironments();
    }

    public void load() {
        loadEntities();
        loadEnvironments();
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
}