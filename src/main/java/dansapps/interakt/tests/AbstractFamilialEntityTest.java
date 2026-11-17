package dansapps.interakt.tests;

import dansapps.interakt.objects.AbstractFamilialEntity;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Characterization tests for the genealogy kept by AbstractFamilialEntity: adding and removing a
 * parent or child reports whether the set changed, the parent and child sets are kept apart, and
 * the comma-separated UUID strings join every ID with ", " and are empty when there are none.
 * The entity under test is an anonymous subclass, so nothing here depends on Actor's behaviour.
 */
public class AbstractFamilialEntityTest {
    private final AbstractFamilialEntity entity = new AbstractFamilialEntity("Gerald") {
    };

    @Test
    public void testNewEntityHasNoParentsOrChildren() {
        Assert.assertTrue(entity.getParentUUIDs().isEmpty());
        Assert.assertTrue(entity.getChildUUIDs().isEmpty());
        Assert.assertEquals("", entity.getParentsUUIDsSeparatedByCommas());
        Assert.assertEquals("", entity.getChildrenUUIDsSeparatedByCommas());
    }

    @Test
    public void testAddParentReportsWhetherTheParentWasNew() {
        UUID parent = UUID.randomUUID();

        Assert.assertTrue(entity.addParent(parent));
        Assert.assertFalse(entity.addParent(parent));
        Assert.assertEquals(1, entity.getParentUUIDs().size());
        Assert.assertTrue(entity.getParentUUIDs().contains(parent));
    }

    @Test
    public void testRemoveParentReportsWhetherTheParentWasPresent() {
        UUID parent = UUID.randomUUID();
        entity.addParent(parent);

        Assert.assertFalse(entity.removeParent(UUID.randomUUID()));
        Assert.assertTrue(entity.removeParent(parent));
        Assert.assertFalse(entity.removeParent(parent));
        Assert.assertTrue(entity.getParentUUIDs().isEmpty());
    }

    @Test
    public void testAddChildReportsWhetherTheChildWasNew() {
        UUID child = UUID.randomUUID();

        Assert.assertTrue(entity.addChild(child));
        Assert.assertFalse(entity.addChild(child));
        Assert.assertEquals(1, entity.getChildUUIDs().size());
        Assert.assertTrue(entity.getChildUUIDs().contains(child));
    }

    @Test
    public void testRemoveChildReportsWhetherTheChildWasPresent() {
        UUID child = UUID.randomUUID();
        entity.addChild(child);

        Assert.assertFalse(entity.removeChild(UUID.randomUUID()));
        Assert.assertTrue(entity.removeChild(child));
        Assert.assertFalse(entity.removeChild(child));
        Assert.assertTrue(entity.getChildUUIDs().isEmpty());
    }

    @Test
    public void testParentsAndChildrenAreKeptApart() {
        UUID parent = UUID.randomUUID();
        UUID child = UUID.randomUUID();

        entity.addParent(parent);
        entity.addChild(child);

        Assert.assertFalse(entity.getParentUUIDs().contains(child));
        Assert.assertFalse(entity.getChildUUIDs().contains(parent));
        Assert.assertFalse(entity.removeChild(parent));
        Assert.assertFalse(entity.removeParent(child));
        Assert.assertEquals(parent.toString(), entity.getParentsUUIDsSeparatedByCommas());
        Assert.assertEquals(child.toString(), entity.getChildrenUUIDsSeparatedByCommas());
    }

    @Test
    public void testGettersReturnTheLiveSets() {
        UUID parent = UUID.randomUUID();
        UUID child = UUID.randomUUID();

        entity.getParentUUIDs().add(parent);
        entity.getChildUUIDs().add(child);

        Assert.assertTrue(entity.removeParent(parent));
        Assert.assertTrue(entity.removeChild(child));
    }

    @Test
    public void testParentsUUIDsAreJoinedWithCommaAndSpace() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();
        entity.addParent(first);
        entity.addParent(second);
        entity.addParent(third);

        String joined = entity.getParentsUUIDsSeparatedByCommas();

        assertJoinedInSetOrder(entity.getParentUUIDs(), joined);
        Assert.assertEquals(new HashSet<>(Arrays.asList(first.toString(), second.toString(), third.toString())),
                new HashSet<>(Arrays.asList(joined.split(", "))));
        Assert.assertFalse(joined.endsWith(", "));
    }

    @Test
    public void testChildrenUUIDsAreJoinedWithCommaAndSpace() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        entity.addChild(first);
        entity.addChild(second);

        String joined = entity.getChildrenUUIDsSeparatedByCommas();

        assertJoinedInSetOrder(entity.getChildUUIDs(), joined);
        Assert.assertEquals(new HashSet<>(Arrays.asList(first.toString(), second.toString())),
                new HashSet<>(Arrays.asList(joined.split(", "))));
        Assert.assertFalse(joined.endsWith(", "));
    }

    private void assertJoinedInSetOrder(HashSet<UUID> uuids, String joined) {
        StringBuilder expected = new StringBuilder();
        for (UUID uuid : uuids) {
            if (expected.length() > 0) {
                expected.append(", ");
            }
            expected.append(uuid);
        }
        Assert.assertEquals(expected.toString(), joined);
    }
}
