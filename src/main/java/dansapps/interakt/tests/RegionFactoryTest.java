package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.RegionFactory;
import dansapps.interakt.factories.SquareFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Region;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Characterization tests for RegionFactory's registration of new and loaded regions. These assert the
 * behavior the code has today, including that loading a region from saved data registers it without
 * generating any squares.
 */
public class RegionFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final Logger logger = new Logger(new Interakt());
    private final RegionFactory regionFactory = createRegionFactory(persistentData);

    private RegionFactory createRegionFactory(PersistentData data) {
        return new RegionFactory(new SquareFactory(logger, data), logger, data);
    }

    @Test
    public void testCreateRegionReturnsTheUUIDOfARegisteredGridOfTheConfiguredSize() throws Exception {
        UUID environmentUUID = UUID.randomUUID();

        UUID regionUUID = regionFactory.createRegion(environmentUUID);

        Region region = persistentData.getRegion(regionUUID);
        Assert.assertEquals(1, persistentData.getRegions().size());
        Assert.assertEquals(environmentUUID, region.getParentEnvironmentUUID());
        Assert.assertEquals(CONFIG.GRID_SIZE, region.getRows());
        Assert.assertEquals(CONFIG.GRID_SIZE, region.getColumns());
        Assert.assertEquals(CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, region.getLocationUUIDs().size());
        Assert.assertEquals(CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, persistentData.getSquares().size());
    }

    @Test
    public void testEachCreateRegionCallGeneratesASeparateGrid() throws Exception {
        UUID environmentUUID = UUID.randomUUID();

        UUID first = regionFactory.createRegion(environmentUUID);
        UUID second = regionFactory.createRegion(environmentUUID);

        Assert.assertFalse(first.equals(second));
        Assert.assertEquals(2, persistentData.getRegions().size());
        Assert.assertEquals(2 * CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, persistentData.getSquares().size());
        for (UUID locationUUID : persistentData.getRegion(first).getLocationUUIDs()) {
            Assert.assertFalse(persistentData.getRegion(second).getLocationUUIDs().contains(locationUUID));
        }
    }

    @Test
    public void testCreateRegionFromDataRegistersRegionWithoutGeneratingSquares() throws Exception {
        Region original = persistentData.getRegion(regionFactory.createRegion(UUID.randomUUID()));
        PersistentData emptyData = new PersistentData();

        createRegionFactory(emptyData).createRegion(original.save());

        Assert.assertEquals(1, emptyData.getRegions().size());
        Region loaded = emptyData.getRegion(original.getUUID());
        Assert.assertEquals(original.getParentEnvironmentUUID(), loaded.getParentEnvironmentUUID());
        Assert.assertEquals(original.getLocationUUIDs(), loaded.getLocationUUIDs());
        Assert.assertTrue(emptyData.getSquares().isEmpty());
    }
}
