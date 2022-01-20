/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.LocationFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Grid;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Region extends Grid implements Savable {

    public Region(int columns, int rows, UUID parentEnvironmentUUID) {
        super(columns, rows, parentEnvironmentUUID);
        generateGrid();
    }

    public Region(Map<String, String> data) {
        super(-1, -1, null);
        this.load(data);
    }

    public void generateGrid() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                UUID locationUUID = LocationFactory.getInstance().createSquare(i, j, getUUID());
                addLocationUUID(locationUUID);
            }
        }
    }

    @Override
    public Square getLocation(int x, int y) {
        for (UUID locationUUID : getLocationUUIDs()) {
            Square square;
            try {
                square = PersistentData.getInstance().getSquare(locationUUID);
            } catch (Exception e) {
                Logger.getInstance().log("Location of a region wasn't found.");
                return null;
            }
            if (square.getX() == x && square.getY() == y) {
                return square;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("locationUUIDs", gson.toJson(getLocationUUIDs()));
        saveMap.put("columns", gson.toJson(getColumns()));
        saveMap.put("rows", gson.toJson(getRows()));
        saveMap.put("parentEnvironmentUUID", gson.toJson(getParentEnvironmentUUID()));
        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type arrayListTypeUUID = new TypeToken<ArrayList<UUID>>(){}.getType();

        setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
        setLocationUUIDs(gson.fromJson(data.get("locationUUIDs"), arrayListTypeUUID));
        setColumns(Integer.parseInt(data.get("columns")));
        setRows(Integer.parseInt(data.get("rows")));
        setParentEnvironmentUUID(UUID.fromString(gson.fromJson(data.get("parentEnvironmentUUID"), String.class)));
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (int x = 0; x < getColumns(); x++) {
            for (int y = 0; y < getRows(); y++) {
                Square square;
                try {
                    square = getLocation(x, y);
                } catch (Exception e) {
                    toReturn.append("[N/A] ");
                    continue;
                }
                if (square.getEntityUUIDs().size() > 0) {
                    switch(CONFIG.DISPLAY_TYPE) {
                        case SIMPLE:
                            toReturn.append("[x] ");
                            break;
                        case CHARACTER_AT_INDEX_ZERO:
                            UUID entityUUID = square.getEntityUUIDs().iterator().next();
                            Actor actor = PersistentData.getInstance().getActor(entityUUID);
                            toReturn.append("[").append(actor.getName().charAt(0)).append("] ");
                            break;
                        case NUMBER_OF_ENTITIES:
                            int numberOfEntities = square.getEntityUUIDs().size();
                            toReturn.append("[").append(numberOfEntities).append("] ");
                            break;
                    }

                }
                else {
                    toReturn.append("[ ] ");
                }
            }
            toReturn.append("\n");
        }
        return toReturn.toString();
    }
}