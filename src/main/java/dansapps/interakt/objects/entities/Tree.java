package dansapps.interakt.objects.entities;

import dansapps.interakt.objects.abs.Entity;
import preponderous.ponder.system.abs.AbstractCommandSender;

public class Tree extends Entity {

    public Tree(int ID, String name) {
        super(ID, name);
    }

    @Override
    public void sendInfo(AbstractCommandSender sender) {
        // TODO: implement
    }
}