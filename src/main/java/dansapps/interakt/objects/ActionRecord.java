package dansapps.interakt.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansapps.interakt.actions.abs.Action;
import preponderous.ponder.misc.abs.Savable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionRecord implements Savable {
    private UUID uuid;
    private UUID entityUUID;
    private String actionName;
    private LocalDateTime timestamp;

    public ActionRecord(UUID entityUUID, Action action) {
        uuid = UUID.randomUUID();
        this.entityUUID = entityUUID;
        this.actionName = action.getName();
        timestamp = LocalDateTime.now();
    }

    public ActionRecord(Map<String, String> data) {
        this.load(data);
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public String getActionName() {
        return actionName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("uuid", gson.toJson(getUUID()));
        saveMap.put("entityUUID", gson.toJson(getEntityUUID()));
        saveMap.put("actionName", gson.toJson(getActionName()));
        saveMap.put("timestamp", gson.toJson(getTimestamp().toString()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        uuid = UUID.fromString(gson.fromJson(data.get("uuid"), String.class));
        entityUUID = UUID.fromString(gson.fromJson(data.get("entityUUID"), String.class));
        actionName = gson.fromJson(data.get("actionName"), String.class);
        timestamp = LocalDateTime.parse(gson.fromJson(data.get("timestamp"), String.class));
    }
}