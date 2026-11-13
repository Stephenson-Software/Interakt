package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.ActorNotFoundException;
import dansapps.interakt.factories.FoodItemFactory;
import dansapps.interakt.objects.Square;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.utils.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

/**
 * Characterization tests for FoodItemFactory: a food item is added to the square's entities and its
 * appearance is written to the event log. The food item is not an actor, yet the square counts and
 * picks it as one; that is the current behavior described in issue #160 and is pinned here as-is.
 * Every test points the data directory at a temporary directory of its own so the log can be read.
 */
public class FoodItemFactoryTest {
    private final PersistentData persistentData = new PersistentData();
    private File temporaryDirectory;
    private String previousDataDirectory;
    private Logger logger;
    private FoodItemFactory foodItemFactory;
    private Square square;

    @Before
    public void setUp() throws IOException {
        temporaryDirectory = Files.createTempDirectory("interakt-food").toFile();
        previousDataDirectory = System.getProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY);
        System.setProperty(LocalStorageService.DATA_DIRECTORY_PROPERTY, temporaryDirectory.getPath());
        logger = new Logger(new Interakt());
        foodItemFactory = new FoodItemFactory(logger);
        square = new Square(2, 3, UUID.randomUUID(), logger, persistentData);
    }

    @After
    public void tearDown() {
        LocalStorageServiceTest.restoreDataDirectoryProperty(previousDataDirectory);
        delete(temporaryDirectory);
    }

    @Test
    public void testFoodItemIsAddedToTheSquare() {
        foodItemFactory.createFoodItemAt("Gerald", square);

        Assert.assertEquals(1, square.getEntityUUIDs().size());
    }

    @Test
    public void testEachFoodItemIsASeparateEntity() {
        foodItemFactory.createFoodItemAt("Gerald", square);
        foodItemFactory.createFoodItemAt("Gerald", square);

        Assert.assertEquals(2, square.getEntityUUIDs().size());
    }

    @Test
    public void testAppearanceIsWrittenToTheEventLog() throws IOException {
        foodItemFactory.createFoodItemAt("Gerald", square);

        List<String> lines = Files.readAllLines(logger.getFile().toPath(), StandardCharsets.UTF_8);
        Assert.assertEquals(lines.toString(), 1, lines.size());
        Assert.assertTrue(lines.get(0),
                lines.get(0).endsWith("] A new food item called Gerald has appeared at 2, 3."));
    }

    @Test
    public void testFoodItemIsNotRegisteredAsAnActor() {
        foodItemFactory.createFoodItemAt("Gerald", square);

        UUID foodItemUUID = square.getEntityUUIDs().iterator().next();
        Assert.assertTrue(persistentData.getActors().isEmpty());
        try {
            persistentData.getActor(foodItemUUID);
            Assert.fail("A food item was found as an actor.");
        } catch (ActorNotFoundException expected) {
            // The food item lives only on the square.
        }
    }

    // Current behavior, see issue #160: the food item is counted as an actor on its square.
    @Test
    public void testSquareCountsTheFoodItemAsAnActor() {
        foodItemFactory.createFoodItemAt("Gerald", square);

        Assert.assertEquals(1, square.getNumActors());
    }

    // Current behavior, see issue #160: picking a random actor can pick the food item and throw.
    @Test(expected = ActorNotFoundException.class)
    public void testRandomActorOnASquareWithOnlyFoodThrows() throws ActorNotFoundException {
        foodItemFactory.createFoodItemAt("Gerald", square);

        square.getRandomActor();
    }

    private static void delete(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                delete(child);
            }
        }
        file.delete();
    }
}
