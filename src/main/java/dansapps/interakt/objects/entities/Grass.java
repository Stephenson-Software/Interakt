package dansapps.interakt.objects.entities;

import dansapps.interakt.objects.abs.Entity;
import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class Grass extends Entity {

    public Grass(int ID, String name) {
        super(ID, name);
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {

    }
}