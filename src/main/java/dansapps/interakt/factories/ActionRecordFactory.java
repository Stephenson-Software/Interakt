package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ActorNotFoundException;
import dansapps.interakt.misc.enums.ACTION_TYPE;
import dansapps.interakt.objects.ActionRecord;
import dansapps.interakt.objects.Actor;

import java.util.Map;

public class ActionRecordFactory {

    public void createActionRecord(Actor actor, ACTION_TYPE actionType) {
        ActionRecord actionRecord = new ActionRecord(actor.getUUID(), actionType);
        PersistentData.getInstance().addActionRecord(actionRecord);
        actor.addActionRecord(actionRecord);
    }

    public void createActionRecord(Map<String, String> data) {
        ActionRecord actionRecord = new ActionRecord(data);
        PersistentData.getInstance().addActionRecord(actionRecord);

        Actor actor = null;
        try {
            actor = PersistentData.getInstance().getActor(actionRecord.getEntityUUID());
        } catch (ActorNotFoundException actorNotFoundException) {
            // fail silently, this actor is likely dead
            return;
        }
        actor.addActionRecord(actionRecord);
    }
}