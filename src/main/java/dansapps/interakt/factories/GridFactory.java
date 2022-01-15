package dansapps.interakt.factories;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Grid;

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

    public UUID createGrid(int size, UUID environmentUUID) {
        Grid grid = new Grid(size, size, environmentUUID);
        grid.createGrid();
        PersistentData.getInstance().addGrid(grid);
        return grid.getUUID();
    }
}
