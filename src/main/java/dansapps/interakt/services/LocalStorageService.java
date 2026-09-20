/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.*;
import dansapps.interakt.objects.*;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.misc.JsonWriterReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves and loads the application's data as JSON files in the data directory. The data directory
 * is {@value #DEFAULT_DATA_DIRECTORY} unless it is overridden with the {@value #DATA_DIRECTORY_PROPERTY}
 * system property or, failing that, the {@value #DATA_DIRECTORY_ENVIRONMENT_VARIABLE} environment
 * variable - see {@link #getDataDirectory()}. The log file and the usage reporting settings live in
 * the same directory.
 *
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 */
public class LocalStorageService {
    private final ActorFactory actorFactory;
    private final WorldFactory worldFactory;
    private final RegionFactory regionFactory;
    private final SquareFactory squareFactory;
    private final TimePartitionFactory timePartitionFactory;
    private final ActionRecordFactory actionRecordFactory;
    private final EntityRecordFactory entityRecordFactory;
    private final Logger logger;
    private final PersistentData persistentData;

    /** Where the data directory is unless it is overridden. Rooted, so on Windows this is the root of the current drive. */
    public final static String DEFAULT_DATA_DIRECTORY = "/Interakt/";
    /** System property that overrides the data directory, e.g. {@code -Dinterakt.data.dir=/home/me/interakt}. */
    public final static String DATA_DIRECTORY_PROPERTY = "interakt.data.dir";
    /** Environment variable that overrides the data directory when the system property is not set. */
    public final static String DATA_DIRECTORY_ENVIRONMENT_VARIABLE = "INTERAKT_DATA_DIR";
    private final static String ACTORS_FILE_NAME = "actors.json";
    private final static String WORLDS_FILE_NAME = "worlds.json";
    private final static String REGIONS_FILE_NAME = "regions.json";
    private final static String SQUARES_FILE_NAME = "squares.json";
    private final static String TIME_PARTITIONS_FILE_NAME = "timePartitions.json";
    private final static String ACTION_RECORDS_FILE_NAME = "actionRecords.json";
    private final static String ENTITY_RECORDS_FILE_NAME = "entityRecords.json";

    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();
    private final String dataDirectory = getDataDirectory();

    public LocalStorageService(ActorFactory actorFactory, WorldFactory worldFactory, RegionFactory regionFactory, SquareFactory squareFactory, TimePartitionFactory timePartitionFactory, ActionRecordFactory actionRecordFactory, EntityRecordFactory entityRecordFactory, Logger logger, PersistentData persistentData) {
        jsonWriterReader.initialize(dataDirectory);
        this.actorFactory = actorFactory;
        this.worldFactory = worldFactory;
        this.regionFactory = regionFactory;
        this.squareFactory = squareFactory;
        this.timePartitionFactory = timePartitionFactory;
        this.actionRecordFactory = actionRecordFactory;
        this.entityRecordFactory = entityRecordFactory;
        this.logger = logger;
        this.persistentData = persistentData;
    }

    /**
     * The directory the data files, the log file and the usage reporting settings are kept in. The
     * {@value #DATA_DIRECTORY_PROPERTY} system property wins, then the
     * {@value #DATA_DIRECTORY_ENVIRONMENT_VARIABLE} environment variable, then
     * {@value #DEFAULT_DATA_DIRECTORY}.
     * @return The directory, always ending in a separator so that a file name can be appended to it.
     */
    public static String getDataDirectory() {
        return resolveDataDirectory(System.getProperty(DATA_DIRECTORY_PROPERTY), System.getenv(DATA_DIRECTORY_ENVIRONMENT_VARIABLE));
    }

    /**
     * The rule behind {@link #getDataDirectory()}, separated from where the values come from so
     * that it can be tested without touching the environment.
     * @param property The value of the system property, or null if it is not set.
     * @param environmentVariable The value of the environment variable, or null if it is not set.
     * @return The directory to use, always ending in a separator.
     */
    public static String resolveDataDirectory(String property, String environmentVariable) {
        String directory = firstNonBlank(property, environmentVariable);
        if (directory == null) {
            return DEFAULT_DATA_DIRECTORY;
        }
        if (directory.endsWith("/") || directory.endsWith(File.separator)) {
            return directory;
        }
        return directory + File.separator;
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.trim().isEmpty()) {
                return candidate.trim();
            }
        }
        return null;
    }

    public void save() {
        performStorageOperation("saving actors", this::saveActors);
        performStorageOperation("saving worlds", this::saveWorlds);
        performStorageOperation("saving regions", this::saveRegions);
        performStorageOperation("saving squares", this::saveSquares);
        performStorageOperation("saving time partitions", this::saveTimePartitions);
        performStorageOperation("saving action records", this::saveActionRecords);
        performStorageOperation("saving entity records", this::saveEntityRecords);
    }

    public void load() {
        performStorageOperation("loading actors", this::loadActors);
        performStorageOperation("loading worlds", this::loadWorlds);
        performStorageOperation("loading regions", this::loadRegions);
        performStorageOperation("loading squares", this::loadSquares);
        performStorageOperation("loading time partitions", this::loadTimePartitions);
        performStorageOperation("loading action records", this::loadActionRecords);
        performStorageOperation("loading entity records", this::loadEntityRecords);
    }

    /**
     * Runs a single storage operation in isolation, so that a failure names the operation it came
     * from, carries the exception's own detail, and does not prevent the remaining operations from
     * running.
     * @param description What the operation was doing, for use in the error message.
     * @param operation The operation to run.
     */
    private void performStorageOperation(String description, Runnable operation) {
        try {
            operation.run();
        }
        catch(Exception e) {
            logger.logError("Something went wrong when " + description + ": " + e);
        }
    }

    private void saveActors() {
        List<Map<String, String>> actors = new ArrayList<>();
        for (Actor actor : persistentData.getActors()){
            actors.add(actor.save());
        }
        jsonWriterReader.writeOutFiles(actors, ACTORS_FILE_NAME);
    }

    private void saveWorlds() {
        List<Map<String, String>> worlds = new ArrayList<>();
        for (World world : persistentData.getWorlds()){
            worlds.add(world.save());
        }
        jsonWriterReader.writeOutFiles(worlds, WORLDS_FILE_NAME);
    }

    private void saveRegions() {
        List<Map<String, String>> regions = new ArrayList<>();
        for (Region region : persistentData.getRegions()){
            regions.add(region.save());
        }
        jsonWriterReader.writeOutFiles(regions, REGIONS_FILE_NAME);
    }

    private void saveSquares() {
        List<Map<String, String>> squares = new ArrayList<>();
        for (Square square : persistentData.getSquares()){
            squares.add(square.save());
        }
        jsonWriterReader.writeOutFiles(squares, SQUARES_FILE_NAME);
    }

    private void saveTimePartitions() {
        List<Map<String, String>> timeSlots = new ArrayList<>();
        for (TimePartition timePartition : persistentData.getTimePartitions()){
            timeSlots.add(timePartition.save());
        }
        jsonWriterReader.writeOutFiles(timeSlots, TIME_PARTITIONS_FILE_NAME);
    }

    private void saveActionRecords() {
        List<Map<String, String>> actionRecords = new ArrayList<>();
        for (ActionRecord actionRecord : persistentData.getActionRecords()){
            actionRecords.add(actionRecord.save());
        }
        jsonWriterReader.writeOutFiles(actionRecords, ACTION_RECORDS_FILE_NAME);
    }

    private void saveEntityRecords() {
        List<Map<String, String>> entityRecords = new ArrayList<>();
        for (EntityRecord entityRecord : persistentData.getEntityRecords()){
            entityRecords.add(entityRecord.save());
        }
        jsonWriterReader.writeOutFiles(entityRecords, ENTITY_RECORDS_FILE_NAME);
    }

    private void loadActors() {
        persistentData.getActors().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +ACTORS_FILE_NAME);
        for (Map<String, String> actorData : data){
            actorFactory.createActorWithData(actorData);
        }
    }

    private void loadWorlds() {
        persistentData.getWorlds().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +WORLDS_FILE_NAME);
        for (Map<String, String> worldData : data){
            worldFactory.createWorld(worldData);
        }
    }

    private void loadRegions() {
        persistentData.getRegions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +REGIONS_FILE_NAME);
        for (Map<String, String> regionData : data){
            regionFactory.createRegion(regionData);
        }
    }

    private void loadSquares() {
        persistentData.getSquares().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +SQUARES_FILE_NAME);
        for (Map<String, String> squareData : data){
            squareFactory.createSquare(squareData);
        }
    }

    private void loadTimePartitions() {
        persistentData.getTimePartitions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +TIME_PARTITIONS_FILE_NAME);
        for (Map<String, String> timePartitionData : data){
            timePartitionFactory.createTimePartition(timePartitionData);
        }
    }

    private void loadActionRecords() {
        persistentData.getActionRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +ACTION_RECORDS_FILE_NAME);
        for (Map<String, String> actionRecordData : data){
            actionRecordFactory.createActionRecord(actionRecordData);
        }
    }

    private void loadEntityRecords() {
        persistentData.getEntityRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(dataDirectory +ENTITY_RECORDS_FILE_NAME);
        for (Map<String, String> entityRecordData : data){
            entityRecordFactory.createEntityRecord(entityRecordData);
        }
    }
}