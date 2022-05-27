package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.utils.Logger;

import java.util.Map;

public class EntityRecordFactory {
    private final Logger logger;
    private final PersistentData persistentData;

    public EntityRecordFactory(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public void createEntityRecord(Actor actor) {
        EntityRecord entityRecord = new EntityRecord(actor.getUUID(), actor.getName(), logger);
        persistentData.addEntityRecord(entityRecord);
    }

    public void createEntityRecord(Map<String, String> data) {
        EntityRecord entityRecord = new EntityRecord(data);
        persistentData.addEntityRecord(entityRecord);
    }
}