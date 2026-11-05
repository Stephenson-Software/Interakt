package dansapps.interakt.tests;

import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.commands.multi.HelpCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandTest {
    private final List<String> messages = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    @Test
    public void testConsoleHelpTextIncludesElapseCommand() {
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

    @Test
    public void testConsoleHelpTextShowsTheQuotedArgumentsCommandsRequire() {
        new HelpCommand().execute(console);

        Assert.assertTrue(messages.contains("view \"actor|world\" \"name\" - View an actor or world."));
        Assert.assertTrue(messages.contains("relations \"actor\" - See the relations of an actor."));
        Assert.assertTrue(messages.contains("create \"actor|world\" \"name\" - Create an actor or world."));
        Assert.assertTrue(messages.contains("delete \"actor|world\" \"name\" - Delete an actor or world."));
        Assert.assertTrue(messages.contains("place \"actor\" \"world\" - Place an actor in a world."));
    }

    @Test
    public void testUnquotedArgumentsAreRejectedSoHelpMustNotAdvertiseThem() {
        PersistentData persistentData = new PersistentData();
        CreateCommand createCommand = new TestUtilities(persistentData).createCreateCommand();

        Assert.assertFalse(createCommand.execute(console, new String[]{"actor", "Unquoted"}));
        Assert.assertTrue(messages.contains("Arguments must be designated in between quotation marks."));
        Assert.assertEquals(0, persistentData.getNumActors());

        Assert.assertTrue(createCommand.execute(console, new String[]{"\"actor\"", "\"Quoted\""}));
        Assert.assertEquals(1, persistentData.getNumActors());
    }
}
