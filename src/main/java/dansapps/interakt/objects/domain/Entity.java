package dansapps.interakt.objects.domain;

import dansapps.interakt.objects.structural.Slot;
import dansapps.interakt.objects.abs.AbstractEntity;

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
}