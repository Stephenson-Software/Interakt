package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.structural.TwoDimensionalGrid;
import preponderous.ponder.misc.Savable;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.HashSet;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Environment extends Entity implements Savable {
    private TwoDimensionalGrid grid; // TODO: make persistent
    private HashSet<LivingEntity> entities = new HashSet<>(); // TODO: make persistent

    public Environment(int ID, String name, int size) {
        super(ID, name);
        grid = new TwoDimensionalGrid(size, size, 10, 10, this);
        grid.createGrid();
    }

    public Environment(Map<String, String> data) {
        super(data);
        this.load(data);
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
}