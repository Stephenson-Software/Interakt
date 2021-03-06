/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.RegionFactory;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Environment;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class World extends Environment implements Savable {
    private final Logger logger;
    private final PersistentData persistentData;

    public World(String name, RegionFactory regionFactory, Logger logger, PersistentData persistentData) {
        super(name, null);
        setGridUUID(regionFactory.createRegion(getUUID()));
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public World(Map<String, String> data, Logger logger, PersistentData persistentData) {
        super("temp", null);
        this.load(data);
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        sender.sendMessage("UUID: " + getUUID());
        sender.sendMessage("Number of entities: " + getNumEntities());
        sender.sendMessage("Created: " + getCreationDate().toString());
        try {
            sender.sendMessage("Grid:\n" + getGrid());
        } catch (Exception e) {
            sender.sendMessage("Grid: N/A");
        }
    }

    public Square getFirstSquare() {
        Region region;
        try {
            region = persistentData.getRegion(getGridUUID());
        } catch (Exception e) {
            logger.logError("A region wasn't found when attempting to find the first location in " + getName() + ".");
            return null;
        }

        Square square;
        try {
            square = persistentData.getSquare(region.getFirstLocationUUID());
        } catch (Exception e) {
            logger.logError("A square wasn't found when attempting to find the first location of a region.");
            return null;
        }

        return square;
    }

    public Region getGrid() {
        try {
            return persistentData.getRegion(getGridUUID());
        } catch (Exception e) {
            logger.logError("There was a problem fetching a grid from a world's reference.");
            return null;
        }
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("name", gson.toJson(getName()));
        saveMap.put("creationDate", gson.toJson(getCreationDate().toString()));
        saveMap.put("gridUUID", gson.toJson(getGridUUID()));
        saveMap.put("entities", gson.toJson(getEntityUUIDs()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
        setName(gson.fromJson(data.get("name"), String.class));
        setCreationDate(LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class)));
        setGridUUID(UUID.fromString(gson.fromJson(data.get("gridUUID"), String.class)));
        setEntityUUIDs(gson.fromJson(data.get("entities"), hashsetTypeUUID));
    }

    public Square getRandomSquare() {
        Random random = new Random();
        int row = random.nextInt(getGrid().getRows());
        int column = random.nextInt(getGrid().getColumns());
        return getGrid().getLocation(row, column);
    }

    private int getNumSquares() {
        return getGrid().getRows() * getGrid().getColumns();
    }
}