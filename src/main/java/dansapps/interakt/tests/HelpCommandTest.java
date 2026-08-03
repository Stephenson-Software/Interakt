package dansapps.interakt.tests;

import dansapps.interakt.commands.multi.HelpCommand;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandTest {

    @Test
    public void testConsoleHelpTextIncludesElapseCommand() {
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };

        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute(console);

        boolean foundElapseLine = false;
        for (String message : messages) {
            if (message.startsWith("elapse - ")) {
                foundElapseLine = true;
                break;
            }
        }
        Assert.assertTrue(foundElapseLine);
    }
}
