package dansapps.interakt.objects.abs;

import preponderous.ponder.system.abs.AbstractCommandSender;

import java.time.LocalDateTime;

/**
 * @author Daniel Stephenson
 * @since January 7th, 2022
 */
public abstract class AbstractEntity {
    private int ID;
    private String name;
    private LocalDateTime creationDate;

    public AbstractEntity(int ID, String name) {
        this.ID = ID;
        this.name = name;
        creationDate = LocalDateTime.now();
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public abstract void sendInfo(AbstractCommandSender sender);
}