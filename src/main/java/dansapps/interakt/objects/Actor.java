/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ActorNotFoundException;
import dansapps.interakt.exceptions.EntityRecordNotFoundException;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.utils.Logger;
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
public class Actor extends AbstractFamilialEntity implements Savable {
    private final LinkedList<ActionRecord> actionRecords = new LinkedList<>();
    private double health;
    private HashSet<UUID> exploredSquares = new HashSet<>();
    private HashMap<UUID, Integer> relations = new HashMap<>();

    // dependencies
    private final Logger logger;
    private final EventFactory eventFactory;
    private final Interakt interakt;
    private final ActionRecordFactory actionRecordFactory;
    private final ActorFactory actorFactory;
    private final PersistentData persistentData;
    private final FoodItemFactory foodItemFactory;

    public Actor(String name, Logger logger, EventFactory eventFactory, Interakt interakt, ActionRecordFactory actionRecordFactory, ActorFactory actorFactory, PersistentData persistentData, FoodItemFactory foodItemFactory) {
        super(name);

        health = CONFIG.MAX_HEALTH;

        // dependencies
        this.logger = logger;
        this.eventFactory = eventFactory;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
        this.actorFactory = actorFactory;
        this.persistentData = persistentData;
        this.foodItemFactory = foodItemFactory;
    }

    public Actor(Map<String, String> data, Logger logger, EventFactory eventFactory, Interakt interakt, ActionRecordFactory actionRecordFactory, ActorFactory actorFactory, PersistentData persistentData, FoodItemFactory foodItemFactory) {
        super("temp");
        this.load(data);

        // dependencies
        this.logger = logger;
        this.eventFactory = eventFactory;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
        this.actorFactory = actorFactory;
        this.persistentData = persistentData;
        this.foodItemFactory = foodItemFactory;
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage(this.toString());
    }

    public World getWorld() {
        return persistentData.getWorld(getEnvironmentUUID());
    }

    public Location getLocation() {
        try {
            return persistentData.getSquare(getLocationUUID());
        } catch (Exception e) {
            logger.logError("Location for " + getName() + " was not found.");
            return null;
        }
    }

    public Square getSquare() {
        return (Square) getLocation();
    }

    public void rest() {
        double oldHealth = getHealth();
        setHealth(getHealth() * 1.10);
        double regained = getHealth() - oldHealth;

        if (regained > 0) {
            Event event = eventFactory.createEvent(getName() + " rested and regenerated " + regained + " health.");
            logger.logEvent(event);

            if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You rest and regenerate " + regained + " health."); // TODO: fix this never getting hit
            }
        }
        else {
            Event event = eventFactory.createEvent(getName() + " rested.");
            logger.logEvent(event);

            if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You rest for a time."); // TODO: fix this never getting hit
            }
        }

        actionRecordFactory.createActionRecord(this, ACTIONTYPE.REST);
    }

    public void move() {
        Square currentSquare;
        try {
            currentSquare = getSquare();
        } catch (Exception e) {
            logger.logError(getName() + " wanted to move, but their location wasn't found.");
            return;
        }

        Square newSquare;
        try {
            newSquare = currentSquare.getRandomAdjacentLocation();
        } catch (Exception ignored) {
            return;
        }

        if (newSquare == null) {
            return;
        }

        currentSquare.removeActor(this);
        setLocationUUID(newSquare.getUUID());
        newSquare.addActor(this);

        try {
            Event event = eventFactory.createEvent(getName() + " moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + getWorld().getName());
            logger.logEvent(event);

            if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You have moved to " + newSquare.getX() + ", " + newSquare.getY() + " in " + getWorld().getName());
            }
        } catch (Exception e) {
            logger.logError(getName() + " moved, but their environment wasn't found.");
        }

        addSquareIfNotExplored(newSquare, eventFactory);

        actionRecordFactory.createActionRecord(this, ACTIONTYPE.MOVE);
    }

    public void addActionRecord(ActionRecord actionRecord) {
        actionRecords.add(actionRecord);
    }

    public int getNumActionRecords() {
        return actionRecords.size();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
        if (this.health > getMaxHealth()) {
            this.health = getMaxHealth();
        }
    }

    public double getMaxHealth() {
        return CONFIG.MAX_HEALTH;
    }

    public void addSquareIfNotExplored(Square square, EventFactory eventFactory) {
        boolean success = exploredSquares.add(square.getUUID());
        if (success) {
            Event event = eventFactory.createEvent(getName() + " has explored a new square.");
            logger.logEvent(event);
        }
    }

    public boolean isFriend(UUID actorUUID) {
        return getRelation(actorUUID) > 50;
    }

    public boolean isFriend(Actor other) {
        return isFriend(other.getUUID());
    }

    public void befriend() {
        Square square = getSquare();
        if (square == null) {
            logger.logError("A square was unexpectedly null.");
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        Actor target = null;
        try {
            target = square.getRandomActor();
        } catch (ActorNotFoundException actorNotFoundException) {
            return;
        }
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        Event event = eventFactory.createEvent(getName() + " was friendly to " + target.getName());
        logger.logEvent(event);

        if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You were friendly to " + target.getName() + ".");
        }

        if (target.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(getName() + " was friendly to you.");
        }

        actionRecordFactory.createActionRecord(this, ACTIONTYPE.BEFRIEND);

        increaseRelation(target, new Random().nextInt(10));
        target.increaseRelation(this, new Random().nextInt(10));
    }

    public int getNumExploredChunks() {
        return exploredSquares.size();
    }

    public int getNumFriends() {
        int count = 0;
        for (UUID actorUUID : relations.keySet()) {
            if (isFriend(actorUUID)) {
                count++;
            }
        }
        return count;
    }

    public void attack() {
        Square square = getSquare();
        if (square == null) {
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        Actor target = null;
        try {
            target = square.getRandomActor();
        } catch (ActorNotFoundException actorNotFoundException) {
            return;
        }
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        if (isFriend(target)) {
            return;
        }

        int damage = new Random().nextInt((int) CONFIG.MAX_DAMAGE);
        target.setHealth(target.getHealth() - damage);

        Event event = eventFactory.createEvent(getName() + " has attacked " + target.getName() + " and dealt " + damage + " damage.");
        logger.logEvent(event);

        if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You have attacked " + target.getName() + " and dealt " + damage + " damage.");
        }

        if (target.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(getName() + " attacked you and and dealt " + damage + " damage.");
        }

        actionRecordFactory.createActionRecord(this, ACTIONTYPE.ATTACK);

        checkForDeath(target);

        target.decreaseRelation(this, (int) (damage * 0.10));
    }

    private void checkForDeath(Actor victim) {
        if (victim.isDead()) {
            Event deathEvent = new Event(victim.getName() + " has died.");
            logger.logEvent(deathEvent);

            if (victim.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
                interakt.getCommandSender().sendMessage("You have died. Type 'quit' to quit the application.");
            }

            Square square = victim.getSquare();
            foodItemFactory.createFoodItemAt(victim.getName(), square);
        }
    }

    public void reproduce() {
        Square square = getSquare();
        if (square == null) {
            return;
        }
        if (square.getNumActors() < 2) {
            return;
        }
        Actor target = null;
        try {
            target = square.getRandomActor();
        } catch (ActorNotFoundException actorNotFoundException) {
            return;
        }
        if (target == null) {
            return;
        }
        if (target.getUUID().equals(getUUID())) {
            return;
        }
        if (!isFriend(target)) {
            return;
        }
        Actor offspring = actorFactory.createActorFromParents(this, target);
        World world = getWorld();
        boolean success = persistentData.placeIntoEnvironment(world, offspring); // TODO: place in same square as parents

        if (!success) {
            logger.logError("Something went wrong placing a new offspring into their environment.");
            return;
        }

        Event event = eventFactory.createEvent(getName() + " reproduced with " + target.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        logger.logEvent(event);

        if (getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You reproduced with " + target.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        }

        if (target.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(target.getName() + " reproduced with you, resulting in " + offspring.getName() + " coming into existence.");
        }

        actionRecordFactory.createActionRecord(this, ACTIONTYPE.REPRODUCE);

        increaseRelation(target, 100);
        target.increaseRelation(this, 100);
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int getRelation(UUID actorUUID) {
        if (!relations.containsKey(actorUUID)) {
            relations.put(actorUUID, 0);
            return 0;
        }
        return relations.get(actorUUID);
    }

    public int getRelation(Actor actor) {
        return getRelation(actor.getUUID());
    }

    public void setRelation(Actor actor, int newRelation) {
        if (!relations.containsKey(actor.getUUID())) {
            relations.put(actor.getUUID(), newRelation);
        }
        else {
            relations.replace(actor.getUUID(), newRelation);
        }
    }

    public void increaseRelation(Actor actor, int amount) {
        int toSet = getRelation(actor) + amount;
        if (toSet > 100) {
            toSet = 100;
        }
        setRelation(actor, toSet);
    }

    public void decreaseRelation(Actor actor, int amount) {
        int toSet = getRelation(actor) - amount;
        if (toSet < -100) {
            toSet = -100;
        }
        setRelation(actor, toSet);
    }

    public String getRelationsString() {
        String toReturn = "";
        for (UUID actorUUID : relations.keySet()) {
            Actor actor = null;
            try {
                actor = persistentData.getActor(actorUUID);
            } catch (ActorNotFoundException actorNotFoundException) {
                try {
                    EntityRecord entityRecord = persistentData.getEntityRecord(actorUUID);
                    toReturn += entityRecord.getName() + ": [deceased]" + "\n";
                } catch (EntityRecordNotFoundException entityRecordNotFoundException) {
                    // this shouldn't happen
                }
                continue;
            }
            try {
                toReturn += actor.getName() + ": " + getRelation(actor) + "\n";
            } catch(NullPointerException e) {
                toReturn += "N/A: " + relations.get(actorUUID) + "\n";
            }

        }
        return toReturn;
    }

    @Override
    public String toString() {
        String toReturn = "=== Details of " + getName() + " ===" + "\n" +
                "Health: " + getHealth() + "/" + getMaxHealth() + "\n" +
                getWorldInfo() + "\n" +
                getSquareInfo() + "\n" +
                "Created: " + getCreationDate().toString() + "\n" +
                "Num times moved: " + getNumTimesMoved() + "\n" +
                "Num squares explored: " + exploredSquares.size() + "\n" +
                "Num friends: " + getNumFriends() + "\n";
        if (CONFIG.SHOW_LINEAGE_INFO) {
            if (parentIDs.size() > 0) {
                toReturn += "Parents: " + getParentNamesSeparatedByCommas() + "\n";
            }
            if (childIDs.size() > 0) {
                toReturn += "Children: " + getChildrenNamesSeparatedByCommas();
            }
        }
        return toReturn;
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
        saveMap.put("exploredSquares", gson.toJson(exploredSquares));
        saveMap.put("health", gson.toJson(health));
        saveMap.put("parentIDs", gson.toJson(parentIDs));
        saveMap.put("childIDs", gson.toJson(childIDs));
        saveMap.put("relations", gson.toJson(relations));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type hashsetTypeUUID = new TypeToken<HashSet<UUID>>(){}.getType();
        Type hashMapTypeUUIDToInteger = new TypeToken<HashMap<UUID, Integer>>(){}.getType();

        try {
            setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
            setName(gson.fromJson(data.get("name"), String.class));
            setCreationDate(LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class)));
            attemptToLoadWorld(gson, data);
            attemptToLoadSquare(gson, data);
            exploredSquares = gson.fromJson(data.get("exploredSquares"), hashsetTypeUUID);
            health = Double.parseDouble(gson.fromJson(data.get("health"), String.class));
            parentIDs = gson.fromJson(data.get("parentIDs"), hashsetTypeUUID);
            childIDs = gson.fromJson(data.get("childIDs"), hashsetTypeUUID);
            relations = gson.fromJson(data.get("relations"), hashMapTypeUUIDToInteger);
        }
        catch(Exception e) {
            logger.logError("Something went wrong loading an actor.");
        }
    }

    private String getParentNamesSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : parentIDs) {
            Actor actor = null;
            try {
                actor = persistentData.getActor(uuid);
            } catch (ActorNotFoundException actorNotFoundException) {
                try {
                    EntityRecord entityRecord = persistentData.getEntityRecord(uuid);
                    toReturn += entityRecord.getName() + "[deceased]";
                    count++;
                    if (count != parentIDs.size()) {
                        toReturn = toReturn + ", ";
                        continue;
                    }
                    else {
                        return toReturn;
                    }
                } catch (EntityRecordNotFoundException entityRecordNotFoundException) {
                    // this shouldn't happen
                }
            }
            if (actor == null) {
                continue;
            }
            toReturn = toReturn + actor.getName();
            count++;
            if (count != parentIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    private String getChildrenNamesSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : childIDs) {
            Actor actor = null;
            try {
                actor = persistentData.getActor(uuid);
            } catch (ActorNotFoundException actorNotFoundException) {
                try {
                    EntityRecord entityRecord = persistentData.getEntityRecord(uuid);
                    toReturn = toReturn + entityRecord.getName() + " [deceased]";
                    count++;
                    if (count != childIDs.size()) {
                        toReturn = toReturn + ", ";
                    }
                    continue;
                } catch (EntityRecordNotFoundException entityRecordNotFoundException) {
                    // this shouldn't happen
                }
            }
            if (actor == null) {
                continue;
            }
            toReturn = toReturn + actor.getName();
            count++;
            if (count != childIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
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
            logger.logError("An environment wasn't found for " + getName());
        }
    }

    private void attemptToLoadSquare(Gson gson, Map<String, String> data) {
        try {
            setLocationUUID(UUID.fromString(gson.fromJson(data.get("locationUUID"), String.class)));
        }
        catch(Exception ignored) {
            logger.logError("A location wasn't found for " + getName());
        }
    }
}