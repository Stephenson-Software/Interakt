package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.domain.Grid;

import java.util.UUID;

public class GridFactory {
    private static GridFactory instance;
    private static int GRID_SIZE = 2;

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
