package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Region;
import dansapps.interakt.objects.Square;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Characterization tests for Square's neighbour lookups, actor bookkeeping and persistence round
 * trip. These assert the behavior the code has today, including that a missing neighbour (off the
 * edge of the grid) comes back as null.
 */
public class SquareTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final Logger logger = new Logger(new Interakt());

    private Region createGrid() {
        testUtilities.createWorld("Earth");
        try {
            World world = persistentData.getWorld("Earth");
            return world.getGrid();
        } catch (Exception e) {
            Assert.fail("World Earth was not found after creation.");
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

    private static void assertAt(Square square, int x, int y) {
        Assert.assertNotNull("Expected a square at (" + x + ", " + y + ")", square);
        Assert.assertEquals(x, square.getX());
        Assert.assertEquals(y, square.getY());
    }

    @Test
    public void testNeighboursOfAnInteriorSquare() {
        Region grid = createGrid();
        Square square = grid.getLocation(4, 6);

        assertAt(square.getUp(grid), 4, 7);
        assertAt(square.getRight(grid), 5, 6);
        assertAt(square.getDown(grid), 4, 5);
        assertAt(square.getLeft(grid), 3, 6);
    }

    @Test
    public void testNeighboursOffTheEdgeOfTheGridAreNull() {
        Region grid = createGrid();
        int last = CONFIG.GRID_SIZE - 1;

        Square origin = grid.getLocation(0, 0);
        Assert.assertNull(origin.getDown(grid));
        Assert.assertNull(origin.getLeft(grid));
        assertAt(origin.getUp(grid), 0, 1);
        assertAt(origin.getRight(grid), 1, 0);

        Square corner = grid.getLocation(last, last);
        Assert.assertNull(corner.getUp(grid));
        Assert.assertNull(corner.getRight(grid));
    }

    @Test
    public void testRandomAdjacentLocationOfAnInteriorSquareIsAnOrthogonalNeighbour() {
        Region grid = createGrid();
        Square square = grid.getLocation(4, 6);

        for (int i = 0; i < 50; i++) {
            Square adjacent = square.getRandomAdjacentLocation();
            Assert.assertNotNull(adjacent);
            int distance = Math.abs(adjacent.getX() - 4) + Math.abs(adjacent.getY() - 6);
            Assert.assertEquals(adjacent.toString(), 1, distance);
        }
    }

    @Test
    public void testParentGridIsLookedUpInPersistentData() {
        Region grid = createGrid();
        Square square = grid.getLocation(2, 3);

        Assert.assertSame(grid, square.getParentGrid());
    }

    @Test
    public void testParentGridIsNullWhenTheRegionIsMissing() {
        Region grid = createGrid();
        Square orphan = new Square(grid.getLocation(2, 3).save(), logger, new PersistentData());

        Assert.assertNull(orphan.getParentGrid());
    }

    @Test
    public void testAddActorRecordsTheActorAndSetsItsLocation() throws Exception {
        Region grid = createGrid();
        Square square = grid.getLocation(2, 3);
        Actor actor = createActor("Gerald");

        square.addActor(actor);

        Assert.assertTrue(square.isActorPresent(actor));
        Assert.assertEquals(1, square.getNumActors());
        Assert.assertEquals(square.getUUID(), actor.getLocationUUID());
        Assert.assertSame(actor, square.getRandomActor());
    }

    @Test
    public void testRemoveActorLeavesTheActorsLocationUntouched() {
        Region grid = createGrid();
        Square square = grid.getLocation(2, 3);
        Actor actor = createActor("Gerald");
        square.addActor(actor);

        square.removeActor(actor);

        Assert.assertFalse(square.isActorPresent(actor));
        Assert.assertEquals(0, square.getNumActors());
        Assert.assertEquals(square.getUUID(), actor.getLocationUUID());
    }

    @Test
    public void testSaveAndLoadRoundTripPreservesFields() {
        Region grid = createGrid();
        Square square = grid.getLocation(2, 3);
        Actor actor = createActor("Gerald");
        square.addActor(actor);

        Map<String, String> saved = square.save();
        Square loaded = new Square(saved, logger, persistentData);

        Assert.assertEquals(square.getUUID(), loaded.getUUID());
        Assert.assertEquals(2, loaded.getX());
        Assert.assertEquals(3, loaded.getY());
        Assert.assertEquals(grid.getUUID(), loaded.getParentGridUUID());
        Assert.assertEquals(1, loaded.getEntityUUIDs().size());
        Assert.assertTrue(loaded.getEntityUUIDs().contains(actor.getUUID()));
    }

    @Test
    public void testToStringIsTheCoordinatePair() {
        Region grid = createGrid();

        Assert.assertEquals("(2, 3)", grid.getLocation(2, 3).toString());
    }
}
