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
    private final Interakt interakt = new Interakt();
    private final Logger logger = new Logger(interakt);
    private final PersistentData persistentData = new PersistentData();
    private final TimePartitionFactory timePartitionFactory = new TimePartitionFactory(persistentData);
    private final LocalTimeService timeService = new LocalTimeService(interakt, timePartitionFactory, logger, persistentData);

    @Test
    public void testElapseCommand() {
        int before = persistentData.getSecondsElapsed();
        ElapseCommand elapseCommand = new ElapseCommand(timeService);
        Console console = new Console();
        elapseCommand.execute(console);

        int after = persistentData.getSecondsElapsed();

        Assert.assertTrue(after > before);
    }
}