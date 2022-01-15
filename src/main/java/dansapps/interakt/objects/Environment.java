/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.data.PersistentData;
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
public class Environment implements Savable {
    private UUID uuid;
    private String name;
    private LocalDateTime creationDate;
    private UUID gridUUID;
    private HashSet<UUID> entities = new HashSet<>();

    public Environment(String name) {
        this(name, 10);
    }

    public Environment(String name, int size) {
        uuid = UUID.randomUUID();
        this.name = name;
        creationDate = LocalDateTime.now();
        Grid grid = new Grid(size, size, getUUID());
        grid.createGrid();
        PersistentData.getInstance().addGrid(grid);
        gridUUID = grid.getUUID();
    }

    public Environment(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getGridUUID() {
        return gridUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void addEntity(Entity entity) {
        entities.add(entity.getUUID());
        entity.setEnvironmentUUID(getUUID());
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getUUID());
    }

    public boolean isEntityPresent(Entity entity) {
        return entities.contains(entity);
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        sender.sendMessage("UUID: " + getUUID());
        sender.sendMessage("Number of entities: " + entities.size());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("creationDate", gson.toJson(creationDate.toString()));
        saveMap.put("gridUUID", gson.toJson(gridUUID));
        saveMap.put("entities", gson.toJson(entities));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        name = gson.fromJson(data.get("name"), String.class);
        creationDate = LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class));
        gridUUID = UUID.fromString(gson.fromJson(data.get("gridUUID"), String.class));
        entities = gson.fromJson(data.get("entities"), hashsetTypeUUID);
    }
}