/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.objects.abs.Edible;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.util.HashSet;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class LivingEntity extends Entity implements Savable {
    private HashSet<Edible> diet = new HashSet<>(); // TODO: make persistent

    public LivingEntity(String name) {
        super(name);
    }

    public LivingEntity(Map<String, String> data) {
        super(data);
        this.load(data);
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

    public void removeFromDiet(Edible edibleEntity) {
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