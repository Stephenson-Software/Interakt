/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Environment;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
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