package dansapps.interakt.tests;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import org.junit.Assert;
import org.junit.Test;

public class CreateCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);

    @Test
    public void testCreateActor() {
        String name = "Gerald";
        testUtilities.createActor(name);
        try {
            persistentData.getActor(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateWorld() {
        String name = "Test";
        testUtilities.createWorld(name);
        try {
            persistentData.getWorld(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
