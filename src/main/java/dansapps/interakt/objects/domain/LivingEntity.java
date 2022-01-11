/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.objects.abs.Edible;
import dansapps.interakt.objects.structural.Location;
import preponderous.ponder.misc.Savable;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.HashSet;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class LivingEntity extends Entity implements Savable {
    private Location gridSlot; // TODO: make persistent
    private HashSet<Edible> diet = new HashSet<>(); // TODO: make persistent

    public LivingEntity(int ID, String name) {
        super(ID, name);
    }

    public LivingEntity(Map<String, String> data) {
        super(data);
        this.load(data);
    }

    public Location getSlot() {
        return gridSlot;
    }

    public void setSlot(Location gridSlot) {
        this.gridSlot = gridSlot;
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        if (getSlot() == null) {
            sender.sendMessage("Location: N/A");
        }
        else {
            sender.sendMessage("Location: " + getSlot().getParentGrid().getParentEnvironment().getName() + " at (" + gridSlot.getX() + ", " + gridSlot.getY() + ")");
        }
        sender.sendMessage("ID: " + getID());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }

    public HashSet<Edible> getDiet() {
        return diet;
    }

    public void setDiet(HashSet<Edible> diet) {
        this.diet = diet;
    }

    public void addToDiet(Edible edibleEntity) {
        diet.add(edibleEntity);
    }

    public void removeFroMDiet(Edible edibleEntity) {
        diet.remove(edibleEntity);
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = super.save();
        // TODO: make other fields persistent

        return saveMap;
    }

    @Override
    public void load(Map<String, String> map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // TODO: make other fields persistent
    }
}