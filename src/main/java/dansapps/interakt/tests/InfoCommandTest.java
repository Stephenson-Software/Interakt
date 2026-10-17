package dansapps.interakt.tests;

import dansapps.interakt.commands.console.InfoCommand;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Characterization tests for InfoCommand: its label and permission, the info lines it sends,
 * and that arguments are ignored.
 */
public class InfoCommandTest {
    private final InfoCommand infoCommand = new InfoCommand();
    private final List<String> messages = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    private final List<String> expectedMessages = Arrays.asList(
            "=== Interakt Info ===",
            "Developer: Daniel Stephenson",
            "Source: https://github.com/Stephenson-Software/Interakt",
            "Dependencies: Ponder, EnvironmentLib"
    );

    @Test
    public void testNamesAndPermission() {
        Assert.assertEquals(Collections.singletonList("info"), infoCommand.getNames());
        Assert.assertEquals(Collections.singletonList("interakt.info"), infoCommand.getPermissions());
    }

    @Test
    public void testExecuteSendsInfo() {
        boolean success = infoCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertEquals(expectedMessages, messages);
    }

    @Test
    public void testArgumentsAreIgnored() {
        boolean success = infoCommand.execute(console, new String[]{"\"unexpected\"", "extra"});

        Assert.assertTrue(success);
        Assert.assertEquals(expectedMessages, messages);
    }
}
