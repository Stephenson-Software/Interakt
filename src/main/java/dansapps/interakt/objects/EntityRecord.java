package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.utils.Logger;
import preponderous.ponder.misc.abs.Savable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityRecord implements Savable {
    private Logger logger;
    private UUID entityUUID;
    private String name;

    public EntityRecord(UUID uuid, String name, Logger logger) {
        setUUID(uuid);
        setName(name);
        this.logger = logger;
    }

    public EntityRecord(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return entityUUID;
    }

    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("name", gson.toJson(getName()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            setUUID(UUID.fromString(gson.fromJson(data.get("uuid"), String.class)));
            setName(gson.fromJson(data.get("name"), String.class));
        }
        catch(Exception e) {
            logger.logError("Something went wrong loading an actor record.");
        }
    }

    private void setUUID(UUID uuid) {
        this.entityUUID = uuid;
    }

    private void setName(String name) {
        this.name = name;
    }
}