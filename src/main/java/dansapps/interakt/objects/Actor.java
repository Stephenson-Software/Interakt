/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.actions.MoveAction;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Entity;
import preponderous.environmentlib.abs.objects.Location;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Actor extends Entity implements Savable {
    private final LinkedList<ActionRecord> actionRecords = new LinkedList<>();
    private int chanceToFight = 50;
    private int chanceToBefriend = 50;
    private HashSet<UUID> friends = new HashSet<>();
    private int numOffspring = 0;
    private int numKills = 0;
    private int numFriends = 0;

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
        sendWorldInfo(sender);
        sendSquareInfo(sender);
    }

    public World getWorld() {
        return PersistentData.getInstance().getWorld(getEnvironmentUUID());
    }

    public Location getLocation() {
        try {
            return PersistentData.getInstance().getSquare(getLocationUUID());
        } catch (Exception e) {
            Logger.getInstance().logError("Location for " + getName() + " was not found.");
            return null;
        }
    }

    public Square getSquare() {
        return (Square) getLocation();
    }

    public void attemptToPerformMoveAction() {
        MoveAction.execute(this);
        ActionRecordFactory.getInstance().createActionRecord(this, new MoveAction());
    }

    public void addActionRecord(ActionRecord actionRecord) {
        actionRecords.add(actionRecord);
    }

    public int getChanceToFight() {
        return chanceToFight;
    }

    public void setChanceToFight(int chanceToFight) {
        this.chanceToFight = chanceToFight;
    }

    public int getChanceToBefriend() {
        return chanceToBefriend;
    }

    public void setChanceToBefriend(int chanceToBefriend) {
        this.chanceToBefriend = chanceToBefriend;
    }

    public HashSet<UUID> getFriends() {
        return friends;
    }

    public void setFriends(HashSet<UUID> friends) {
        this.friends = friends;
    }

    public int getNumOffspring() {
        return numOffspring;
    }

    public void setNumOffspring(int numOffspring) {
        this.numOffspring = numOffspring;
    }

    public int getNumKills() {
        return numKills;
    }

    public void setNumKills(int numKills) {
        this.numKills = numKills;
    }

    public int getNumFriends() {
        return numFriends;
    }

    public void setNumFriends(int numFriends) {
        this.numFriends = numFriends;
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
        attemptToLoadWorld(gson, data);
        attemptToLoadSquare(gson, data);
    }

    private void sendWorldInfo(CommandSender sender) {
        if (getEnvironmentUUID() == null) {
            sender.sendMessage("World: N/A");
        }
        else {
            sender.sendMessage("World: " + getWorld().getName());
        }
    }

    private void sendSquareInfo(CommandSender sender) {
        try {
            Square square = getSquare();
            sender.sendMessage("Square: " + square);
        } catch (Exception e) {
            sender.sendMessage("Square: N/A");
        }
    }

    private void attemptToLoadWorld(Gson gson, Map<String, String> data) {
        try {
            setEnvironmentUUID(UUID.fromString(gson.fromJson(data.get("environmentUUID"), String.class)));
        }
        catch(Exception ignored) {
            Logger.getInstance().logError("An environment wasn't found for " + getName());
        }
    }

    private void attemptToLoadSquare(Gson gson, Map<String, String> data) {
        try {
            setLocationUUID(UUID.fromString(gson.fromJson(data.get("locationUUID"), String.class)));
        }
        catch(Exception ignored) {
            Logger.getInstance().logError("A location wasn't found for " + getName());
        }
    }
}