/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Grid;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
import java.util.UUID;

public class GridFactory {
    private static GridFactory instance;
    private static final int GRID_SIZE = 3;

    private GridFactory() {

    }

    public static GridFactory getInstance() {
        if (instance == null) {
            instance = new GridFactory();
        }
        return instance;
    }

    public UUID createGrid(UUID environmentUUID) {
        Grid grid = new Grid(GRID_SIZE, GRID_SIZE, environmentUUID);
        PersistentData.getInstance().addGrid(grid);
        return grid.getUUID();
    }
}
