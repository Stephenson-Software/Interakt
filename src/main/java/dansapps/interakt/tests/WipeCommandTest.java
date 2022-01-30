package dansapps.interakt.tests;

import dansapps.interakt.commands.WipeCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.User;
import org.junit.Assert;
import org.junit.Test;

public class WipeCommandTest {

    @Test
    public void testWipeCommand() {
        Utilities.getInstance().createWorld("TestWorld");
        Utilities.getInstance().createActor("TestActor");

        WipeCommand wipeCommand = new WipeCommand();
        User user = new User();
        wipeCommand.execute(user);

        Assert.assertEquals(0, PersistentData.getInstance().getNumActors());
        Assert.assertEquals(0, PersistentData.getInstance().getNumWorlds());
    }
}