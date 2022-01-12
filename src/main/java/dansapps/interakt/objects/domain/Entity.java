/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Entity implements Savable {
    private int ID;
    private String name;
    private LocalDateTime creationDate;

    public Entity(int ID, String name) {
        this.ID = ID;
        this.name = name;
        creationDate = LocalDateTime.now();
    }

    public Entity(Map<String, String> data) {
        this.load(data);
    }

    public int getID() {
        return ID;
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

    public void sendInfo(CommandSender sender) {
        // TODO: implement
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("name", gson.toJson(name));
        // saveMap.put("creationDate", gson.toJson(creationDate)); // TODO: fix

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        name = gson.fromJson(data.get("name"), String.class);
        // creationDate = LocalDateTime.parse(gson.fromJson(data.get("creationDate"), String.class)); // TODO: fix
    }
}