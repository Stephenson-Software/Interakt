package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.factories.RegionFactory;
import dansapps.interakt.factories.SquareFactory;
import dansapps.interakt.factories.WorldFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Region;
import dansapps.interakt.objects.World;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

/**
 * Characterization tests for WorldFactory's name handling and the registration it performs for new
 * and loaded worlds. These assert the behavior the code has today, including that a taken name is
 * rejected when creating by name but not checked at all when creating from saved data.
 */
public class WorldFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final Logger logger = new Logger(new Interakt());
    private final WorldFactory worldFactory = createWorldFactory(persistentData);

    private WorldFactory createWorldFactory(PersistentData data) {
        return new WorldFactory(new RegionFactory(new SquareFactory(logger, data), logger, data), logger, data);
    }

    @Test
    public void testCreateWorldWithNameRegistersWorldAndItsGrid() throws Exception {
        worldFactory.createWorld("Earth");

        World world = persistentData.getWorld("Earth");
        Assert.assertEquals(1, persistentData.getNumWorlds());
        Assert.assertEquals(1, persistentData.getRegions().size());
        Region grid = persistentData.getRegion(world.getGridUUID());
        Assert.assertEquals(world.getUUID(), grid.getParentEnvironmentUUID());
        Assert.assertEquals(CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, persistentData.getSquares().size());
    }

    @Test
    public void testCreateWorldWithTakenNameThrowsAndLeavesDataUnchanged() throws Exception {
        worldFactory.createWorld("Earth");
        World original = persistentData.getWorld("Earth");

        // The name check is case-insensitive and runs before construction, so no grid is generated either.
        try {
            worldFactory.createWorld("EARTH");
            Assert.fail("Expected NameTakenException");
        } catch (NameTakenException expected) {
            // expected
        }

        Assert.assertEquals(1, persistentData.getNumWorlds());
        Assert.assertSame(original, persistentData.getWorld("Earth"));
        Assert.assertEquals("Earth", original.getName());
        Assert.assertEquals(1, persistentData.getRegions().size());
        Assert.assertEquals(CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, persistentData.getSquares().size());
    }

    @Test
    public void testCreateWorldFromDataRegistersWorldWithoutGeneratingAGrid() throws Exception {
        worldFactory.createWorld("Earth");
        World original = persistentData.getWorld("Earth");
        PersistentData emptyData = new PersistentData();

        createWorldFactory(emptyData).createWorld(original.save());

        Assert.assertEquals(1, emptyData.getNumWorlds());
        World loaded = emptyData.getWorld(original.getUUID());
        Assert.assertNotNull(loaded);
        Assert.assertEquals("Earth", loaded.getName());
        Assert.assertEquals(original.getGridUUID(), loaded.getGridUUID());
        Assert.assertTrue(emptyData.getRegions().isEmpty());
        Assert.assertTrue(emptyData.getSquares().isEmpty());
    }

    @Test
    public void testCreateWorldFromDataDoesNotCheckForATakenName() throws Exception {
        worldFactory.createWorld("Earth");
        Map<String, String> data = persistentData.getWorld("Earth").save();
        UUID otherUUID = UUID.randomUUID();
        data.put("uuid", "\"" + otherUUID + "\"");

        worldFactory.createWorld(data);

        Assert.assertEquals(2, persistentData.getNumWorlds());
        Assert.assertEquals("Earth", persistentData.getWorld(otherUUID).getName());
    }
}
