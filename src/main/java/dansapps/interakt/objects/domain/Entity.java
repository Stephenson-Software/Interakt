package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.abs.AbstractEntity;
import dansapps.interakt.objects.structural.Slot;
import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class Entity extends AbstractEntity {

    private Slot slot;

    public Entity(int ID, String name) {
        super(ID, name);
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        sender.sendMessage("=== Details of " + getName() + " ===");
        if (getSlot() == null) {
            sender.sendMessage("Location: N/A");
        }
        else {
            sender.sendMessage("Location: (" + slot.getX() + ", " + slot.getY() + ")");
        }
        sender.sendMessage("ID: " + getID());
        sender.sendMessage("Created: " + getCreationDate().toString());
    }
}