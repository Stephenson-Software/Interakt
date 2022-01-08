package dansapps.interakt.objects.structural;

import dansapps.interakt.objects.domain.Environment;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class Grid {
    private int columns;
    private int rows;

    private int gridSlotHeight;
    private int gridSlotWidth;

    private Slot primarySlot;

    private Environment parentEnvironment;

    public Grid(int columns, int rows, int gridSlotHeight, int gridSlotWidth, Environment parentEnvironment) {
        this.columns = columns;
        this.rows = rows;
        this.gridSlotHeight = gridSlotHeight;
        this.gridSlotWidth = gridSlotWidth;
        this.parentEnvironment = parentEnvironment;
    }

    private HashSet<Slot> slots = new HashSet<>();

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

    public HashSet<Slot> getSlots() {
        return slots;
    }

    public void setSlots(HashSet<Slot> slots) {
        this.slots = slots;
    }

    public Slot getPrimarySlot() {
        return primarySlot;
    }

    public void setPrimarySlot(Slot primarySlot) {
        this.primarySlot = primarySlot;
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
                Slot newSlot = new Slot(xPosition, yPosition, gridSlotWidth, gridSlotHeight, this);
                addSlot(newSlot);
                if (i == 0 && j == 0) {
                    primarySlot = newSlot;
                }
                xPosition += gridSlotWidth;
            }
            yPosition += gridSlotHeight;
            xPosition = 0;
        }
    }

    public void addSlot(Slot slot) {
        // TODO: ensure that no slots are added with the same x and y
        slots.add(slot);
    }


}