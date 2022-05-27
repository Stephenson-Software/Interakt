package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.utils.Logger;

import java.util.Map;

public class EntityRecordFactory {
    private Logger logger;

    public EntityRecordFactory(Logger logger) {
        this.logger = logger;
    }

    public void createEntityRecord(Actor actor) {
        EntityRecord entityRecord = new EntityRecord(actor.getUUID(), actor.getName(), logger);
        PersistentData.getInstance().addEntityRecord(entityRecord);
    }

    public void createEntityRecord(Map<String, String> data) {
        EntityRecord entityRecord = new EntityRecord(data);
        PersistentData.getInstance().addEntityRecord(entityRecord);
    }
}