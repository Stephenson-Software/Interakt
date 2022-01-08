package dansapps.interakt.objects.entities;

import dansapps.interakt.objects.abs.Entity;
import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public class BerryBush extends Entity {

    public BerryBush(int ID, String name) {
        super(ID, name);
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        // TODO: implement
    }
}