package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Entity;

public class EntityFactory {
    private static EntityFactory instance;

    private EntityFactory() {

    }

    public static EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    public void createEntity(String name) {
        Entity entity = new Entity(name);
        PersistentData.getInstance().addEntity(entity);
    }
}