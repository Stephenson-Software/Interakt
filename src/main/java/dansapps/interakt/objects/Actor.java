/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.actions.MoveAction;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Entity;
import preponderous.environmentlib.abs.objects.Location;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Actor extends Entity implements Savable {
    private LinkedList<UUID> associatedActionRecordUUIDs = new LinkedList<>();

    public Actor(String name) {
        super(name);
    }

    public Actor(Map<String, String> data) {
        super("temp");
        this.load(data);
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
            return;
        }

        try {
            sender.sendMessage("Environment: " + getEnvironment().getName());
        }
        catch (Exception e) {
            sender.sendMessage("Environment: ERROR");
        }
    }

    private void sendLocationInfo(CommandSender sender) {
        try {
            Square square = getSquare();
            sender.sendMessage("Location: " + square);
        } catch (Exception e) {
            sender.sendMessage("Location: N/A");
        }
    }

    public World getEnvironment() {
        return PersistentData.getInstance().getEnvironment(getEnvironmentUUID());
    }

    public Location getLocation() {
        try {
            return PersistentData.getInstance().getLocation(getLocationUUID());
        } catch (Exception e) {
            Logger.getInstance().log("Location for " + getName() + " was not found.");
            return null;
        }
    }

    public Square getSquare() {
        return (Square) getLocation();
    }

    public void attemptToPerformMoveAction() {
        MoveAction.execute(this);
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("name", gson.toJson(getName()));
        saveMap.put("creationDate", gson.toJson(getCreationDate().toString()));
        saveMap.put("environmentUUID", gson.toJson(getEnvironmentUUID()));
        saveMap.put("locationUUID", gson.toJson(getLocationUUID()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
        setName(gson.fromJson(data.get("name"), String.class));
        setCreationDate(LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class)));
        attemptToLoadEnvironment(gson, data);
        attemptToLoadLocation(gson, data);
    }

    private void attemptToLoadEnvironment(Gson gson, Map<String, String> data) {
        try {
            setEnvironmentUUID(UUID.fromString(gson.fromJson(data.get("environmentUUID"), String.class)));
        }
        catch(Exception ignored) {
            Logger.getInstance().log("An environment wasn't found for " + getName());
        }
    }

    private void attemptToLoadLocation(Gson gson, Map<String, String> data) {
        try {
            setLocationUUID(UUID.fromString(gson.fromJson(data.get("locationUUID"), String.class)));
        }
        catch(Exception ignored) {
            Logger.getInstance().log("A location wasn't found for " + getName());
        }
    }
}