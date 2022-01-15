/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.actions.MoveAction;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Entity implements Savable {
    private UUID uuid;
    private String name;
    private LocalDateTime creationDate;
    private UUID environmentUUID;
    private UUID locationUUID;

    public Entity(String name) {
        uuid = UUID.randomUUID();
        this.name = name;
        creationDate = LocalDateTime.now();
    }

    public Entity(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
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

    public UUID getEnvironmentUUID() {
        return environmentUUID;
    }

    public void setEnvironmentUUID(UUID environmentUUID) {
        this.environmentUUID = environmentUUID;
    }

    private UUID getLocationUUID() {
        return locationUUID;
    }

    public void setLocationUUID(UUID locationUUD) {
        this.locationUUID = locationUUD;
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        sender.sendMessage("UUID: " + getUUID());
        sender.sendMessage("Created: " + getCreationDate().toString());
        sendEnvironmentInfo(sender);
        sendLocationInfo(sender);
    }

    private void sendEnvironmentInfo(CommandSender sender) {
        if (getEnvironmentUUID() == null) {
            sender.sendMessage("Environment: N/A");
        }
        else {
            sender.sendMessage("Environment: " + getEnvironment().getName());
        }
    }

    private void sendLocationInfo(CommandSender sender) {
        try {
            Location location = getLocation();
            sender.sendMessage("Location: " + location);
        } catch (Exception e) {
            sender.sendMessage("Location: N/A");
        }
    }

    public Environment getEnvironment() {
        return PersistentData.getInstance().getEnvironment(getEnvironmentUUID());
    }

    public Location getLocation() throws Exception {
        return PersistentData.getInstance().getLocation(getLocationUUID());
    }

    public void attemptToPerformMoveAction() {
        MoveAction.execute(this);
        Location location;
        try {
            location = getLocation();
        } catch (Exception e) {
            return;
        }
        Logger.getInstance().log(getName() + " moved to " + location.getX() + ", " + location.getY() + " in " + getEnvironment().getName());
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(uuid));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("creationDate", gson.toJson(creationDate.toString()));
        saveMap.put("environmentUUID", gson.toJson(environmentUUID));
        saveMap.put("locationUUID", gson.toJson(locationUUID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        name = gson.fromJson(data.get("name"), String.class);
        creationDate = LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class));
        attemptToLoadEnvironment(gson, data);
        attemptToLoadLocation(gson, data);
    }

    private void attemptToLoadEnvironment(Gson gson, Map<String, String> data) {
        try {
            environmentUUID = UUID.fromString(gson.fromJson(data.get("environmentUUID"), String.class));
        }
        catch(Exception ignored) {

        }
    }

    private void attemptToLoadLocation(Gson gson, Map<String, String> data) {
        try {
            locationUUID = UUID.fromString(gson.fromJson(data.get("locationUUID"), String.class));
        }
        catch(Exception ignored) {

        }
    }
}