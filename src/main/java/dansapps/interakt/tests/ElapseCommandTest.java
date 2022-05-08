package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.ElapseCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

public class ElapseCommandTest {

    @Test
    public void testElapseCommand() {
        Interakt interakt = new Interakt();

        int before = PersistentData.getInstance().getSecondsElapsed();

        ElapseCommand elapseCommand = new ElapseCommand();
        Console console = new Console();
        elapseCommand.execute(console);

        int after = PersistentData.getInstance().getSecondsElapsed();

        Assert.assertTrue(after > before);
    }
}