package dansapps.interakt.tests;

import dansapps.interakt.commands.console.GenerateTestDataCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

public class GenerateTestDataCommandTest {

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand();
        Console console = new Console();
        generateTestDataCommand.execute(console);

        Assert.assertTrue(PersistentData.getInstance().getNumWorlds() > 0);
        Assert.assertTrue(PersistentData.getInstance().getNumActors() > 0);
    }
}