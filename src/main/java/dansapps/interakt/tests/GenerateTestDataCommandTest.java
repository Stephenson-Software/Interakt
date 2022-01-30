package dansapps.interakt.tests;

import dansapps.interakt.commands.GenerateTestDataCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.User;
import org.junit.Assert;
import org.junit.Test;

public class GenerateTestDataCommandTest {

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand();
        User user = new User();
        generateTestDataCommand.execute(user);

        Assert.assertTrue(PersistentData.getInstance().getNumWorlds() > 0);
        Assert.assertTrue(PersistentData.getInstance().getNumActors() > 0);
    }
}