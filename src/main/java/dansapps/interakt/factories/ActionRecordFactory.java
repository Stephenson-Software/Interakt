package dansapps.interakt.factories;

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

    public void createActionRecord() {
        // TODO: implement
    }
}