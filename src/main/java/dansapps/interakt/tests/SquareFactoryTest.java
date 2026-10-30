package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.SquareFactory;
import dansapps.interakt.objects.Square;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Characterization tests for SquareFactory's registration of new and loaded squares. These assert the
 * behavior the code has today, including that the grid UUID passed in is stored without being checked
 * against the regions in persistent data.
 */
public class SquareFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private final Logger logger = new Logger(new Interakt());
    private final SquareFactory squareFactory = new SquareFactory(logger, persistentData);

    @Test
    public void testCreateSquareReturnsTheUUIDOfARegisteredSquare() throws Exception {
        UUID gridUUID = UUID.randomUUID();

        UUID squareUUID = squareFactory.createSquare(4, 7, gridUUID);

        Square square = persistentData.getSquare(squareUUID);
        Assert.assertEquals(1, persistentData.getSquares().size());
        Assert.assertEquals(4, square.getX());
        Assert.assertEquals(7, square.getY());
        Assert.assertEquals(gridUUID, square.getParentGridUUID());
        Assert.assertTrue(persistentData.getRegions().isEmpty());
        Assert.assertNull(square.getParentGrid());
    }

    @Test
    public void testCreateSquareFromDataRegistersTheLoadedSquare() throws Exception {
        Square original = persistentData.getSquare(squareFactory.createSquare(2, 5, UUID.randomUUID()));
        PersistentData emptyData = new PersistentData();

        new SquareFactory(logger, emptyData).createSquare(original.save());

        Assert.assertEquals(1, emptyData.getSquares().size());
        Square loaded = emptyData.getSquare(original.getUUID());
        Assert.assertEquals(2, loaded.getX());
        Assert.assertEquals(5, loaded.getY());
        Assert.assertEquals(original.getParentGridUUID(), loaded.getParentGridUUID());
    }
}
