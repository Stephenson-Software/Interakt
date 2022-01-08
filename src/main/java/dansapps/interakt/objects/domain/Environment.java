package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.Entity;
import dansapps.interakt.objects.structural.TwoDimensionalGrid;
import preponderous.ponder.misc.Savable;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.HashSet;
import java.util.Map;

public class Environment extends Entity implements Savable {
    private TwoDimensionalGrid grid;
    private HashSet<LivingEntity> entities = new HashSet<>();

    public Environment(int ID, String name, int size) {
        super(ID, name);
        grid = new TwoDimensionalGrid(size, size, 10, 10, this);
        grid.createGrid();
    }

    public TwoDimensionalGrid getGrid() {
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

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> map) {
        // TODO: implement
    }
}