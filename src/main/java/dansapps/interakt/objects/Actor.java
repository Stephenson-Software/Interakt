/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.actions.MoveAction;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.abs.objects.Entity;
import preponderous.environmentlib.abs.objects.Location;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Actor extends Entity implements Savable {
    private final LinkedList<ActionRecord> actionRecords = new LinkedList<>();
    private int moveChanceThreshold;

    // unused
    private HashSet<UUID> exploredSquares = new HashSet<>();
    private final HashSet<UUID> friends = new HashSet<>();
    private final Personality personality = new Personality();
    private final Statistics statistics = new Statistics();

    public Actor(String name) {
        super(name);
        moveChanceThreshold = new Random().nextInt(CONFIG.MAX_CHANCE_TO_MOVE);
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
        sender.sendMessage("Num times moved: " + getNumTimesMoved());
        sender.sendMessage("Chance to move: " + getMoveChanceThreshold());
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

    public void performMoveActionIfRollSuccessful() {
        if (roll(getMoveChanceThreshold())) {
            MoveAction.execute(this);
        }
    }

    private boolean roll(int threshold) {
        Random random = new Random();
        int result = random.nextInt(100);
        return result < threshold;
    }

    public void addActionRecord(ActionRecord actionRecord) {
        actionRecords.add(actionRecord);
    }

    public int getMoveChanceThreshold() {
        return moveChanceThreshold;
    }

    public HashSet<UUID> getFriends() {
        return friends;
    }

    public Personality getPersonality() {
        return personality;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    private int getNumTimesMoved() {
        int count = 0;
        for (ActionRecord actionRecord : actionRecords) {
            if (actionRecord.getActionName().equals("move")) {
                count++;
            }
        }
        return count;
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
        saveMap.put("moveChanceThreshold", gson.toJson(moveChanceThreshold));
        saveMap.put("exploredSquares", gson.toJson(exploredSquares));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();

        try {
            setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
            setName(gson.fromJson(data.get("name"), String.class));
            setCreationDate(LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class)));
            attemptToLoadWorld(gson, data);
            attemptToLoadSquare(gson, data);
            moveChanceThreshold = Integer.parseInt(gson.fromJson(data.get("moveChanceThreshold"), String.class));
            exploredSquares = gson.fromJson(data.get("exploredSquares"), hashsetTypeUUID);
        }
        catch(Exception e) {
            Logger.getInstance().logError("Something went wrong loading an actor.");
        }
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

    public int getNumActionRecords() {
        return actionRecords.size();
    }

    private static class Personality {
        private int chanceToFight = 50;
        private int chanceToBefriend = 50;

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
    }

    private static class Statistics {
        private int numOffspring = 0;
        private int numKills = 0;
        private int numFriends = 0;

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
    }
}