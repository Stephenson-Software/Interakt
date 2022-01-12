/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.structural.TwoDimensionalGrid;
import preponderous.ponder.misc.abs.Savable;
import preponderous.ponder.system.abs.CommandSender;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Environment extends Entity implements Savable {
    private UUID uuid;
    private TwoDimensionalGrid grid; // TODO: make persistent
    private HashSet<LivingEntity> entities = new HashSet<>(); // TODO: make persistent

    public Environment(String name, int size) {
        super(name);
        uuid = UUID.randomUUID();
        grid = new TwoDimensionalGrid(size, size, 10, 10, uuid);
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
        entity.setLocationUUID(getGrid().getPrimaryLocationUUID());
    }

    public void removeEntity(LivingEntity entity) {
        entities.remove(entity);
    }

    public boolean isEntityPresent(LivingEntity entity) {
        return entities.contains(entity);
    }

    @Override
    public void sendInfo(CommandSender sender) {
        sender.sendMessage("===  Details of " + getName() + " ===");
        sender.sendMessage("UUID: " + getUUID());
        sender.sendMessage("Number of slots: " + grid.getLocations().size());
        sender.sendMessage("Number of entities: " + entities.size());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }
}