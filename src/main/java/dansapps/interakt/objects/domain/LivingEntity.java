package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.Edible;
import dansapps.interakt.objects.abs.Entity;
import dansapps.interakt.objects.structural.GridSlot;
import preponderous.ponder.misc.Savable;
import preponderous.ponder.system.abs.AbstractCommandSender;

import java.util.HashSet;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class LivingEntity extends Entity implements Savable {
    private GridSlot gridSlot;
    private HashSet<Edible> diet = new HashSet<>();

    public LivingEntity(int ID, String name) {
        super(ID, name);
    }

    public GridSlot getSlot() {
        return gridSlot;
    }

    public void setSlot(GridSlot gridSlot) {
        this.gridSlot = gridSlot;
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        if (getSlot() == null) {
            sender.sendMessage("Location: N/A");
        }
        else {
            sender.sendMessage("Location: " + getSlot().getParentGrid().getParentEnvironment().getName() + " at (" + gridSlot.getX() + ", " + gridSlot.getY() + ")");
        }
        sender.sendMessage("ID: " + getID());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }

    public HashSet<Edible> getDiet() {
        return diet;
    }

    public void setDiet(HashSet<Edible> diet) {
        this.diet = diet;
    }

    public void addToDiet(Edible edibleEntity) {
        diet.add(edibleEntity);
    }

    public void removeFroMDiet(Edible edibleEntity) {
        diet.remove(edibleEntity);
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