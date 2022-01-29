package dansapps.interakt.tests;

import dansapps.interakt.commands.CreateCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.User;
import org.junit.Assert;
import org.junit.Test;

public class CreateCommandTest {

    @Test
    public void testCreateActor() {
        String name = "Gerald";
        Utilities.getInstance().createActor(name);
        try {
            PersistentData.getInstance().getActor(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateWorld() {
        String name = "Test";
        Utilities.getInstance().createWorld(name);
        try {
            PersistentData.getInstance().getWorld(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
