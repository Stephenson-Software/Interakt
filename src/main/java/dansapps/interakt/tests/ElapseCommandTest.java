package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.console.ElapseCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.TimePartitionFactory;
import dansapps.interakt.services.LocalTimeService;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

public class ElapseCommandTest {

    @Test
    public void testElapseCommand() {
        Interakt interakt = new Interakt();
        TimePartitionFactory timePartitionFactory = new TimePartitionFactory();
        Logger logger = new Logger(interakt);

        LocalTimeService timeService = new LocalTimeService(interakt, timePartitionFactory, logger);

        int before = PersistentData.getInstance().getSecondsElapsed();

        ElapseCommand elapseCommand = new ElapseCommand(timeService);
        Console console = new Console();
        elapseCommand.execute(console);

        int after = PersistentData.getInstance().getSecondsElapsed();

        Assert.assertTrue(after > before);
    }
}