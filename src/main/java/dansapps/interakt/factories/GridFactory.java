/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Grid;
import dansapps.interakt.misc.CONFIG;

import java.util.UUID;

public class GridFactory {
    private static GridFactory instance;

    private GridFactory() {

    }

    public static GridFactory getInstance() {
        if (instance == null) {
            instance = new GridFactory();
        }
        return instance;
    }

    public UUID createGrid(UUID environmentUUID) {
        Grid grid = new Grid(CONFIG.GRID_SIZE, CONFIG.GRID_SIZE, environmentUUID);
        PersistentData.getInstance().addGrid(grid);
        return grid.getUUID();
    }
}
