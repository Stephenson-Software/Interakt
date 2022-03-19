/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.actions.AttackAction;
import dansapps.interakt.actions.BefriendAction;
import dansapps.interakt.actions.MoveAction;
import dansapps.interakt.actions.ReproduceAction;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.EventFactory;
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
    private int chanceToMove;
    private int chanceToBefriend;
    private int chanceToAttack;
    private int chanceToReproduce;
    private double health;
    private HashSet<UUID> exploredSquares = new HashSet<>();
    private HashSet<UUID> friends = new HashSet<>();

    public Actor(String name) {
        super(name);
        chanceToMove = new Random().nextInt(CONFIG.MAX_CHANCE_TO_MOVE);
        chanceToBefriend = new Random().nextInt(CONFIG.MAX_CHANCE_TO_BEFRIEND);
        chanceToAttack = new Random().nextInt(CONFIG.MAX_CHANCE_TO_ATTACK);
        chanceToReproduce = new Random().nextInt(CONFIG.MAX_CHANCE_TO_REPRODUCE);
        health = CONFIG.MAX_HEALTH;
    }

    public Actor(Map<String, String> data) {
        super("temp");
        this.load(data);
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage(this.toString());
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

    public void attemptToMove() {
        if (roll(getChanceToMove())) {
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

    public int getChanceToMove() {
        return chanceToMove;
    }

    public int getChanceToBefriend() {
        return chanceToBefriend;
    }

    public int getChanceToAttack() {
        return chanceToAttack;
    }

    private int getChanceToReproduce() {
        return chanceToReproduce;
    }

    public HashSet<UUID> getFriends() {
        return friends;
    }

    public int getNumActionRecords() {
        return actionRecords.size();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return CONFIG.MAX_HEALTH;
    }

    public void addSquareIfNotExplored(Square square) {
        boolean success = exploredSquares.add(square.getUUID());
        if (success) {
            Event event = EventFactory.getInstance().createEvent(getName() + " has explored a new square.");
            Logger.getInstance().logEvent(event);
        }
    }

    public void addFriend(Actor actor) {
        friends.add(actor.getUUID());
    }

    public void removeFriend(Actor actor) {
        friends.remove(actor.getUUID());
    }

    public boolean isFriend(Actor other) {
        return friends.contains(other.getUUID());
    }

    public void attemptToBefriend() {
        Square square = getSquare();
        if (square == null) {
            Logger.getInstance().logError("A square was unexpectedly null.");
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        if (!roll(getChanceToBefriend())) {
            return;
        }
        Actor target = square.getRandomActor();
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        BefriendAction.execute(this, target);
     }

    public int getNumExploredChunks() {
        return exploredSquares.size();
    }

    public int getNumFriends() {
        return friends.size();
    }

    public void attemptToAttack() {
        Square square = getSquare();
        if (square == null) {
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        if (!roll(getChanceToAttack())) {
            return;
        }
        Actor target = square.getRandomActor();
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        AttackAction.execute(this, target);
    }

    public void attemptToPerformReproduceAction() {
        Square square = getSquare();
        if (square == null) {
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        if (!roll(getChanceToReproduce())) {
            return;
        }
        Actor target = square.getRandomActor();
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        ReproduceAction.execute(this, target);
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    @Override
    public String toString() {
        return "=== Details of " + getName() + " ===" + "\n" +
                "Health: " + getHealth() + "/" + getMaxHealth() + "\n" +
                getWorldInfo() + "\n" +
                getSquareInfo() + "\n" +
                "Created: " + getCreationDate().toString() + "\n" +
                "Chance to move: " + getChanceToMove() + "\n" +
                "Chance to befriend: " + getChanceToBefriend() + "\n" +
                "Chance to attack: " + getChanceToAttack() + "\n" +
                "Num times moved: " + getNumTimesMoved() + "\n" +
                "Num squares explored: " + exploredSquares.size() + "\n" +
                "Num friends: " + friends.size();
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
        saveMap.put("moveChanceThreshold", gson.toJson(chanceToMove));
        saveMap.put("exploredSquares", gson.toJson(exploredSquares));
        saveMap.put("health", gson.toJson(health));
        saveMap.put("friends", gson.toJson(friends));
        saveMap.put("chanceToBefriend", gson.toJson(chanceToBefriend));
        saveMap.put("chanceToAttack", gson.toJson(chanceToAttack));

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
            chanceToMove = Integer.parseInt(gson.fromJson(data.get("moveChanceThreshold"), String.class));
            exploredSquares = gson.fromJson(data.get("exploredSquares"), hashsetTypeUUID);
            health = Double.parseDouble(gson.fromJson(data.get("health"), String.class));
            friends = gson.fromJson(data.get("friends"), hashsetTypeUUID);
            chanceToBefriend = Integer.parseInt(gson.fromJson(data.get("chanceToBefriend"), String.class));
            chanceToAttack = Integer.parseInt(gson.fromJson(data.get("chanceToAttack"), String.class));
        }
        catch(Exception e) {
            Logger.getInstance().logError("Something went wrong loading an actor.");
        }
    }

    private String getWorldInfo() {
        if (getEnvironmentUUID() == null) {
            return "World: N/A";
        }
        else {
            return "World: " + getWorld().getName();
        }
    }

    private String getSquareInfo() {
        try {
            Square square = getSquare();
            return "Square: " + square;
        } catch (Exception e) {
            return "Square: N/A";
        }
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