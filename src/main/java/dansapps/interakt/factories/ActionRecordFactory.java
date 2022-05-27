package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ActorNotFoundException;
import dansapps.interakt.misc.enums.ACTIONTYPE;
import dansapps.interakt.objects.ActionRecord;
import dansapps.interakt.objects.Actor;

import java.util.Map;

public class ActionRecordFactory {
    private final PersistentData persistentData;

    public ActionRecordFactory(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public void createActionRecord(Actor actor, ACTIONTYPE actionType) {
        ActionRecord actionRecord = new ActionRecord(actor.getUUID(), actionType);
        persistentData.addActionRecord(actionRecord);
        actor.addActionRecord(actionRecord);
    }

    public void createActionRecord(Map<String, String> data) {
        ActionRecord actionRecord = new ActionRecord(data);
        persistentData.addActionRecord(actionRecord);

        Actor actor = null;
        try {
            actor = persistentData.getActor(actionRecord.getEntityUUID());
        } catch (ActorNotFoundException actorNotFoundException) {
            // fail silently, this actor is likely dead
            return;
        }
        actor.addActionRecord(actionRecord);
    }
}