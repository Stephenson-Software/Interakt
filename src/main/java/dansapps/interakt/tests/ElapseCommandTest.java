package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.ElapseCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.User;
import org.junit.Test;

public class ElapseCommandTest {

    @Test
    public void testElapseCommand() {
        Interakt interakt = new Interakt();

        int before = PersistentData.getInstance().getSecondsElapsed();

        ElapseCommand elapseCommand = new ElapseCommand();
        User user = new User();
        elapseCommand.execute(user);

        int after = PersistentData.getInstance().getSecondsElapsed();

        assert(after > before);
    }
}