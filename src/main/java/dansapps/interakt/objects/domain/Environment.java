package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.Entity;
import dansapps.interakt.objects.structural.Grid;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.HashSet;

public class Environment extends Entity {
    private Grid grid;
    private HashSet<LivingEntity> entities = new HashSet<>();

    public Environment(int ID, String name, int size) {
        super(ID, name);
        grid = new Grid(size, size, 10, 10, this);
        grid.createGrid();
    }

    public Grid getGrid() {
        return grid;
    }

    public void addEntity(LivingEntity entity) {
        entities.add(entity);
        entity.setSlot(getGrid().getPrimarySlot());
    }

    public void removeEntity(LivingEntity entity) {
        entities.remove(entity);
    }

    public boolean isEntityPresent(LivingEntity entity) {
        return entities.contains(entity);
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        sender.sendMessage("===  Details of " + getName() + " ===");
        sender.sendMessage("Number of slots: " + grid.getSlots().size());
        sender.sendMessage("Number of entities: " + entities.size());
        sender.sendMessage("ID: " + getID());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }
}