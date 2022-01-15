package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Environment;

public class EnvironmentFactory {
    private static EnvironmentFactory instance;

    private EnvironmentFactory() {

    }

    public static EnvironmentFactory getInstance() {
        if (instance == null) {
            instance = new EnvironmentFactory();
        }
        return instance;
    }

    public void createEnvironment(String name) {
        Environment environment = new Environment(name);
        PersistentData.getInstance().addEnvironment(environment);
    }
}