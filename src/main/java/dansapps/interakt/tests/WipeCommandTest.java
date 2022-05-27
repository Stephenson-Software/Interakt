package dansapps.interakt.tests;

import dansapps.interakt.commands.console.WipeCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

public class WipeCommandTest {
    private final TestUtilities testUtilities = new TestUtilities();

    @Test
    public void testWipeCommand() {
        testUtilities.createWorld("TestWorld");
        testUtilities.createActor("TestActor");

        WipeCommand wipeCommand = new WipeCommand();
        Console console = new Console();
        wipeCommand.execute(console);

        Assert.assertEquals(0, PersistentData.getInstance().getNumActors());
        Assert.assertEquals(0, PersistentData.getInstance().getNumWorlds());
    }
}