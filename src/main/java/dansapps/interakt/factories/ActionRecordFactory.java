package dansapps.interakt.factories;

import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.ActionRecord;
import dansapps.interakt.objects.Actor;

import java.util.Map;

public class ActionRecordFactory {
    private static ActionRecordFactory instance;

    private ActionRecordFactory() {

    }

    public static ActionRecordFactory getInstance() {
        if (instance == null) {
            instance = new ActionRecordFactory();
        }
        return instance;
    }

    public void createActionRecord(Actor actor, Action action) {
        ActionRecord actionRecord = new ActionRecord(actor.getUUID(), action);
        PersistentData.getInstance().addActionRecord(actionRecord);
        actor.addActionRecord(actionRecord);
    }

    public void createActionRecord(Map<String, String> data) {
        ActionRecord actionRecord = new ActionRecord(data);
        PersistentData.getInstance().addActionRecord(actionRecord);

        Actor actor = PersistentData.getInstance().getActor(actionRecord.getEntityUUID());
        actor.addActionRecord(actionRecord);
    }
}