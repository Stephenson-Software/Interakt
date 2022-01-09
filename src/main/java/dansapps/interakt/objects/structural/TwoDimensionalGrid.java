package dansapps.interakt.objects.structural;

import dansapps.interakt.objects.domain.Environment;

import java.util.HashSet;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class TwoDimensionalGrid {
    private int columns;
    private int rows;

    private int gridSlotHeight;
    private int gridSlotWidth;

    private GridSlot primaryGridSlot;

    private Environment parentEnvironment;

    public TwoDimensionalGrid(int columns, int rows, int gridSlotHeight, int gridSlotWidth, Environment parentEnvironment) {
        this.columns = columns;
        this.rows = rows;
        this.gridSlotHeight = gridSlotHeight;
        this.gridSlotWidth = gridSlotWidth;
        this.parentEnvironment = parentEnvironment;
    }

    private HashSet<GridSlot> gridSlots = new HashSet<>();

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getGridSlotHeight() {
        return gridSlotHeight;
    }

    public void setGridSlotHeight(int gridSlotHeight) {
        this.gridSlotHeight = gridSlotHeight;
    }

    public int getGridSlotWidth() {
        return gridSlotWidth;
    }

    public void setGridSlotWidth(int gridSlotWidth) {
        this.gridSlotWidth = gridSlotWidth;
    }

    public HashSet<GridSlot> getSlots() {
        return gridSlots;
    }

    public void setSlots(HashSet<GridSlot> gridSlots) {
        this.gridSlots = gridSlots;
    }

    public GridSlot getPrimarySlot() {
        return primaryGridSlot;
    }

    public void setPrimarySlot(GridSlot primaryGridSlot) {
        this.primaryGridSlot = primaryGridSlot;
    }

    public Environment getParentEnvironment() {
        return parentEnvironment;
    }

    public void setParentEnvironment(Environment parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    public void createGrid() {
        int xPosition = 0;
        int yPosition = 0;
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                GridSlot newGridSlot = new GridSlot(xPosition, yPosition, gridSlotWidth, gridSlotHeight, this);
                addSlot(newGridSlot);
                if (i == 0 && j == 0) {
                    primaryGridSlot = newGridSlot;
                }
                xPosition += gridSlotWidth;
            }
            yPosition += gridSlotHeight;
            xPosition = 0;
        }
    }

    public void addSlot(GridSlot gridSlot) {
        // TODO: ensure that no slots are added with the same x and y
        gridSlots.add(gridSlot);
    }
}