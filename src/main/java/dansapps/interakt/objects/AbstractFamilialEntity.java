package dansapps.interakt.objects;

import preponderous.environmentlib.abs.objects.Entity;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public abstract class AbstractFamilialEntity extends Entity {
    protected HashSet<UUID> parentIDs = new HashSet<>();
    protected HashSet<UUID> childIDs = new HashSet<>();

    public AbstractFamilialEntity(String name) {
        super(name);
    }

    public HashSet<UUID> getParentUUIDs() {
        return parentIDs;
    }

    public boolean addParent(UUID uuid) {
        return parentIDs.add(uuid);
    }

    public boolean removeParent(UUID uuid) {
        return parentIDs.remove(uuid);
    }

    public HashSet<UUID> getChildUUIDs() {
        return childIDs;
    }

    public boolean addChild(UUID uuid) {
        return childIDs.add(uuid);
    }

    public boolean removeChild(UUID uuid) {
        return childIDs.remove(uuid);
    }

    public String getParentsUUIDsSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : parentIDs) {
            toReturn = toReturn + uuid.toString();
            count++;
            if (count != parentIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    public String getChildrenUUIDsSeparatedByCommas() {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : childIDs) {
            toReturn = toReturn + uuid.toString();
            count++;
            if (count != childIDs.size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }
}