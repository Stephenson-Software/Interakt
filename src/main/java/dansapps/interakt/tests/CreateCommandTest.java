package dansapps.interakt.tests;

import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import org.junit.Assert;
import org.junit.Test;

public class CreateCommandTest {
    private final TestUtilities testUtilities = new TestUtilities();

    @Test
    public void testCreateActor() {
        String name = "Gerald";
        testUtilities.createActor(name);
        try {
            PersistentData.getInstance().getActor(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateWorld() {
        String name = "Test";
        testUtilities.createWorld(name);
        try {
            PersistentData.getInstance().getWorld(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
