package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.EntityRecord;
import dansapps.interakt.objects.Actor;

import java.util.Map;

public class EntityRecordFactory {
    private static EntityRecordFactory instance;

    private EntityRecordFactory() {

    }

    public static EntityRecordFactory getInstance() {
        if (instance == null) {
            instance = new EntityRecordFactory();
        }
        return instance;
    }

    public void createEntityRecord(Actor actor) {
        EntityRecord entityRecord = new EntityRecord(actor.getUUID(), actor.getName());
        PersistentData.getInstance().addEntityRecord(entityRecord);
    }

    public void createEntityRecord(Map<String, String> data) {
        EntityRecord entityRecord = new EntityRecord(data);
        PersistentData.getInstance().addEntityRecord(entityRecord);
    }
}