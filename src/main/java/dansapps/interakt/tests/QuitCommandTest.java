package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.commands.multi.QuitCommand;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Characterization tests for QuitCommand: its labels and permission, and that it says goodbye
 * before shutting the application down, with or without arguments. The application's
 * shutdownApplication is replaced so that the test run is not ended by System.exit.
 */
public class QuitCommandTest {
    private final List<String> events = new ArrayList<>();
    private final Interakt interakt = new Interakt() {
        @Override
        public void shutdownApplication() {
            events.add("shutdown");
        }
    };
    private final QuitCommand quitCommand = new QuitCommand(interakt);
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            events.add("message: " + message);
        }
    };

    @Test
    public void testNamesAndPermission() {
        Assert.assertEquals(List.of("quit", "exit"), quitCommand.getNames());
        Assert.assertEquals(Collections.singletonList("interakt.quit"), quitCommand.getPermissions());
    }

    @Test
    public void testExecuteSaysGoodbyeThenShutsDown() {
        boolean success = quitCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertEquals(List.of("message: Goodbye!", "shutdown"), events);
    }

    @Test
    public void testArgumentsAreIgnored() {
        boolean success = quitCommand.execute(console, new String[]{"\"now\""});

        Assert.assertTrue(success);
        Assert.assertEquals(List.of("message: Goodbye!", "shutdown"), events);
    }

    @Test
    public void testExitAliasShutsDownThroughTheApplication() {
        // Interakt registers its own QuitCommand, so dispatching the alias through onCommand
        // reaches the overridden shutdownApplication rather than System.exit.
        boolean success = interakt.onCommand(console, "exit", new String[0]);

        Assert.assertTrue(success);
        Assert.assertEquals(List.of("message: Goodbye!", "shutdown"), events);
    }
}
