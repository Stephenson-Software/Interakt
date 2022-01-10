package dansapps.interakt.objects.entities;

import dansapps.interakt.objects.abs.Edible;
import dansapps.interakt.objects.domain.Entity;
import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class BerryBush extends Entity implements Edible {

    public BerryBush(int ID, String name) {
        super(ID, name);
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        // TODO: implement
    }
}