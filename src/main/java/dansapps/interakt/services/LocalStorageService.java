package dansapps.interakt.services;

import preponderous.ponder.misc.JsonWriterReader;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalStorageService {
    private static LocalStorageService instance;
    private final static String FILE_PATH = "./Interakt/";
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
        // TODO: implement
    }

    private void saveEnvironments() {
        // TODO: implement
    }

    private void loadEntities() {
        // TODO: implement
    }

    private void loadEnvironments() {
        // TODO: implement
    }
}