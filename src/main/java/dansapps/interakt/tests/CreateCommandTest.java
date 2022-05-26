package dansapps.interakt.tests;

import dansapps.interakt.data.PersistentData;
import org.junit.Assert;
import org.junit.Test;

public class CreateCommandTest {

    @Test
    public void testCreateActor() {
        String name = "Gerald";
        TestUtilities.getInstance().createActor(name);
        try {
            PersistentData.getInstance().getActor(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateWorld() {
        String name = "Test";
        TestUtilities.getInstance().createWorld(name);
        try {
            PersistentData.getInstance().getWorld(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
