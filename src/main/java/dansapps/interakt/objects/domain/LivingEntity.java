package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.Entity;
import dansapps.interakt.objects.structural.GridSlot;
import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class LivingEntity extends Entity {

    private GridSlot gridSlot;

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
}