/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.GridFactory;
import preponderous.environmentlib.abs.objects.Environment;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class World extends Environment implements Savable {

    public World(String name) {
        super(name, null);
        setGridUUID(GridFactory.getInstance().createGrid(getUUID()));
    }

    public World(Map<String, String> data) {
        super("temp", null);
        this.load(data);
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

    public Square getFirstLocation() throws Exception {
        Region region = PersistentData.getInstance().getGrid(getGridUUID());
        return PersistentData.getInstance().getLocation(region.getFirstLocationUUID());
    }

    @Override
    public Region getGrid() {
        try {
            return PersistentData.getInstance().getGrid(getGridUUID());
        } catch (Exception e) {
            e.printStackTrace();
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
}