package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Region;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Characterization tests for World's grid lookups, persistence round trip and info output. These
 * assert the behavior the code has today, including what happens when a world's region is missing.
 */
public class WorldTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final Logger logger = new Logger(new Interakt());

    private World createWorld(String name) {
        testUtilities.createWorld(name);
        try {
            return persistentData.getWorld(name);
        } catch (Exception e) {
            Assert.fail("World " + name + " was not found after creation.");
            return null;
        }
    }

    private Actor createActor(String name) {
        testUtilities.createActor(name);
        try {
            return persistentData.getActor(name);
        } catch (Exception e) {
            Assert.fail("Actor " + name + " was not found after creation.");
            return null;
        }
    }

    private List<String> sendInfo(World world) {
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
        world.sendInfo(console);
        return messages;
    }

    @Test
    public void testNewWorldHasAGridOfTheConfiguredSize() throws Exception {
        World world = createWorld("Earth");

        Region grid = world.getGrid();
        Assert.assertNotNull(grid);
        Assert.assertSame(persistentData.getRegion(world.getGridUUID()), grid);
        Assert.assertEquals(CONFIG.GRID_SIZE, grid.getRows());
        Assert.assertEquals(CONFIG.GRID_SIZE, grid.getColumns());
        Assert.assertEquals(CONFIG.GRID_SIZE * CONFIG.GRID_SIZE, grid.getLocationUUIDs().size());
        Assert.assertEquals(world.getUUID(), grid.getParentEnvironmentUUID());
    }

    @Test
    public void testGetFirstSquareReturnsTheSquareAtTheOrigin() {
        World world = createWorld("Earth");

        Square square = world.getFirstSquare();

        Assert.assertNotNull(square);
        Assert.assertEquals(0, square.getX());
        Assert.assertEquals(0, square.getY());
        Assert.assertEquals(world.getGridUUID(), square.getParentGridUUID());
    }

    @Test
    public void testGetRandomSquareReturnsASquareOfTheGrid() {
        World world = createWorld("Earth");
        Region grid = world.getGrid();

        for (int i = 0; i < 25; i++) {
            Square square = world.getRandomSquare();
            Assert.assertNotNull(square);
            Assert.assertTrue(grid.getLocationUUIDs().contains(square.getUUID()));
        }
    }

    @Test
    public void testSaveAndLoadRoundTripPreservesFields() {
        World world = createWorld("Earth");
        Actor actor = createActor("Gerald");
        Assert.assertTrue(persistentData.placeIntoEnvironment(world, actor));

        Map<String, String> saved = world.save();
        World loaded = new World(saved, logger, persistentData);

        Assert.assertEquals(world.getUUID(), loaded.getUUID());
        Assert.assertEquals("Earth", loaded.getName());
        Assert.assertEquals(world.getCreationDate(), loaded.getCreationDate());
        Assert.assertEquals(world.getGridUUID(), loaded.getGridUUID());
        Assert.assertEquals(1, loaded.getEntityUUIDs().size());
        Assert.assertTrue(loaded.getEntityUUIDs().contains(actor.getUUID()));
        Assert.assertSame(world.getGrid(), loaded.getGrid());
    }

    @Test
    public void testLookupsReturnNullWhenTheRegionIsMissing() {
        World world = createWorld("Earth");
        World orphan = new World(world.save(), logger, new PersistentData());

        Assert.assertNull(orphan.getGrid());
        Assert.assertNull(orphan.getFirstSquare());
        Assert.assertNull(orphan.getRandomSquare());
    }

    @Test
    public void testPlacementFailsWhenTheRegionIsMissing() {
        World world = createWorld("Earth");
        PersistentData emptyData = new PersistentData();
        World orphan = new World(world.save(), logger, emptyData);
        Actor actor = createActor("Gerald");

        Assert.assertFalse(emptyData.placeIntoEnvironment(orphan, actor));
        Assert.assertEquals(0, orphan.getNumEntities());
    }

    @Test
    public void testSendInfoReportsTheWorldsDetails() {
        World world = createWorld("Earth");

        List<String> messages = sendInfo(world);

        Assert.assertEquals(5, messages.size());
        Assert.assertEquals("=== Details of Earth ===", messages.get(0));
        Assert.assertEquals("UUID: " + world.getUUID(), messages.get(1));
        Assert.assertEquals("Number of entities: 0", messages.get(2));
        Assert.assertEquals("Created: " + world.getCreationDate(), messages.get(3));
        Assert.assertEquals("Grid:\n" + world.getGrid(), messages.get(4));
    }

    @Test
    public void testSendInfoPrintsGridNotAvailableWhenTheRegionIsMissing() {
        World world = createWorld("Earth");
        World orphan = new World(world.save(), logger, new PersistentData());

        List<String> messages = sendInfo(orphan);

        Assert.assertEquals(5, messages.size());
        Assert.assertEquals("Grid: N/A", messages.get(4));
    }
}
