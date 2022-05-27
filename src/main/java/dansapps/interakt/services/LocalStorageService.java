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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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

    public final static String FILE_PATH = "/Interakt/";
    private final static String ACTORS_FILE_NAME = "actors.json";
    private final static String WORLDS_FILE_NAME = "worlds.json";
    private final static String REGIONS_FILE_NAME = "regions.json";
    private final static String SQUARES_FILE_NAME = "squares.json";
    private final static String TIME_PARTITIONS_FILE_NAME = "timePartitions.json";
    private final static String ACTION_RECORDS_FILE_NAME = "actionRecords.json";
    private final static String ENTITY_RECORDS_FILE_NAME = "entityRecords.json";

    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();

    public LocalStorageService(ActorFactory actorFactory, WorldFactory worldFactory, RegionFactory regionFactory, SquareFactory squareFactory, TimePartitionFactory timePartitionFactory, ActionRecordFactory actionRecordFactory, EntityRecordFactory entityRecordFactory, Logger logger, PersistentData persistentData) {
        jsonWriterReader.initialize(FILE_PATH);
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

    public void save() {
        try {
            saveActors();
            saveWorlds();
            saveRegions();
            saveSquares();
            saveTimePartitions();
            saveActionRecords();
            saveEntityRecords();
        }
        catch(Exception e) {
            logger.logError("Something went wrong when saving the data of the application.");
        }
    }

    public void load() {
        try {
            loadActors();
            loadWorlds();
            loadRegions();
            loadSquares();
            loadTimePartitions();
            loadActionRecords();
            loadEntityRecords();
        }
        catch(Exception e) {
            logger.logError("Something went wrong when loading the data of the application.");
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
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ACTORS_FILE_NAME);
        for (Map<String, String> actorData : data){
            actorFactory.createActorWithData(actorData);
        }
    }

    private void loadWorlds() {
        persistentData.getWorlds().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + WORLDS_FILE_NAME);
        for (Map<String, String> worldData : data){
            worldFactory.createWorld(worldData);
        }
    }

    private void loadRegions() {
        persistentData.getRegions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + REGIONS_FILE_NAME);
        for (Map<String, String> regionData : data){
            regionFactory.createRegion(regionData);
        }
    }

    private void loadSquares() {
        persistentData.getSquares().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + SQUARES_FILE_NAME);
        for (Map<String, String> squareData : data){
            squareFactory.createSquare(squareData);
        }
    }

    private void loadTimePartitions() {
        persistentData.getTimePartitions().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + TIME_PARTITIONS_FILE_NAME);
        for (Map<String, String> timePartitionData : data){
            timePartitionFactory.createTimePartition(timePartitionData);
        }
    }

    private void loadActionRecords() {
        persistentData.getActionRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ACTION_RECORDS_FILE_NAME);
        for (Map<String, String> actionRecordData : data){
            actionRecordFactory.createActionRecord(actionRecordData);
        }
    }

    private void loadEntityRecords() {
        persistentData.getEntityRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + ENTITY_RECORDS_FILE_NAME);
        for (Map<String, String> entityRecordData : data){
            entityRecordFactory.createEntityRecord(entityRecordData);
        }
    }
}