package dansapps.interakt.objects;

import dansapps.interakt.actions.abs.Action;
import preponderous.ponder.misc.abs.Savable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ActionRecord implements Savable {
    private final UUID uuid;
    private final UUID entityUUID;
    private final String actionName;
    private final LocalDateTime timestamp;

    public ActionRecord(UUID entityUUID, Action action) {
        uuid = UUID.randomUUID();
        this.entityUUID = entityUUID;
        this.actionName = action.getName();
        timestamp = LocalDateTime.now();
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
        return null;
    }

    @Override
    public void load(Map<String, String> map) {

    }
}