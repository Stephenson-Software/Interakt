package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.SquareFactory;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Region;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Characterization tests for Region's grid generation, coordinate lookup, persistence round trip
 * and text rendering. These assert the behavior the code has today, including that rendering a
 * region whose squares are missing throws rather than printing "[N/A]".
 */
public class RegionTest {
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

    @Test
    public void testGenerateGridCreatesOneSquarePerCellInPersistentData() throws Exception {
        PersistentData data = new PersistentData();
        Region region = new Region(3, 2, UUID.randomUUID(), new SquareFactory(logger, data), logger, data);

        Assert.assertEquals(6, region.getLocationUUIDs().size());
        Assert.assertEquals(6, data.getSquares().size());
        Set<String> coordinates = new HashSet<>();
        for (UUID locationUUID : region.getLocationUUIDs()) {
            Square square = data.getSquare(locationUUID);
            Assert.assertEquals(region.getUUID(), square.getParentGridUUID());
            coordinates.add(square.toString());
        }
        // The outer loop runs over rows and supplies x, so x ranges over the row count.
        Set<String> expected = new HashSet<>();
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                expected.add("(" + x + ", " + y + ")");
            }
        }
        Assert.assertEquals(expected, coordinates);
    }

    @Test
    public void testGetLocationFindsEverySquareByCoordinates() {
        Region grid = createWorld("Earth").getGrid();

        for (int x = 0; x < CONFIG.GRID_SIZE; x++) {
            for (int y = 0; y < CONFIG.GRID_SIZE; y++) {
                Square square = grid.getLocation(x, y);
                Assert.assertNotNull(square);
                Assert.assertEquals(x, square.getX());
                Assert.assertEquals(y, square.getY());
            }
        }
    }

    @Test
    public void testGetLocationOutsideTheGridIsNull() {
        Region grid = createWorld("Earth").getGrid();

        Assert.assertNull(grid.getLocation(-1, 0));
        Assert.assertNull(grid.getLocation(0, CONFIG.GRID_SIZE));
    }

    @Test
    public void testGetLocationIsNullWhenTheSquaresAreMissing() {
        Region grid = createWorld("Earth").getGrid();
        Region orphan = new Region(grid.save(), logger, new PersistentData());

        Assert.assertNull(orphan.getLocation(0, 0));
    }

    @Test
    public void testSaveAndLoadRoundTripPreservesFields() {
        World world = createWorld("Earth");
        Region grid = world.getGrid();

        Map<String, String> saved = grid.save();
        Region loaded = new Region(saved, logger, persistentData);

        Assert.assertEquals(grid.getUUID(), loaded.getUUID());
        Assert.assertEquals(grid.getColumns(), loaded.getColumns());
        Assert.assertEquals(grid.getRows(), loaded.getRows());
        Assert.assertEquals(world.getUUID(), loaded.getParentEnvironmentUUID());
        Assert.assertEquals(grid.getLocationUUIDs(), loaded.getLocationUUIDs());
        Assert.assertSame(grid.getLocation(2, 3), loaded.getLocation(2, 3));
    }

    @Test
    public void testToStringOfAnEmptyGridIsAllBlankCells() {
        Region grid = createWorld("Earth").getGrid();

        StringBuilder row = new StringBuilder();
        for (int i = 0; i < CONFIG.GRID_SIZE; i++) {
            row.append("[ ] ");
        }
        String[] lines = grid.toString().split("\n");

        Assert.assertEquals(CONFIG.GRID_SIZE, lines.length);
        for (String line : lines) {
            Assert.assertEquals(row.toString(), line);
        }
    }

    @Test
    public void testToStringShowsTheFirstLetterOfAnOccupyingActor() {
        Region grid = createWorld("Earth").getGrid();
        grid.getLocation(2, 3).addActor(createActor("Gerald"));

        String[] lines = grid.toString().split("\n");

        // Each output line is one x value; the cells along it are the y values.
        Assert.assertEquals("[ ] [ ] [ ] [G] ", lines[2].substring(0, 16));
        Assert.assertFalse(lines[3], lines[3].contains("[G]"));
        Assert.assertFalse(lines[1], lines[1].contains("[G]"));
    }

    @Test
    public void testToStringShowsNotAvailableCellsWhenTheSquaresAreMissing() {
        Region grid = createWorld("Earth").getGrid();
        Region orphan = new Region(grid.save(), logger, new PersistentData());

        StringBuilder row = new StringBuilder();
        for (int i = 0; i < CONFIG.GRID_SIZE; i++) {
            row.append("[N/A] ");
        }
        String[] lines = orphan.toString().split("\n");

        Assert.assertEquals(CONFIG.GRID_SIZE, lines.length);
        for (String line : lines) {
            Assert.assertEquals(row.toString(), line);
        }
    }
}
