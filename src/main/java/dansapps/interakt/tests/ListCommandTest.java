package dansapps.interakt.tests;

import dansapps.interakt.commands.console.ListCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final List<String> messages = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    @Test
    public void testListWithNoActorsOrWorldsShowsOnlyHeadings() {
        ListCommand listCommand = new ListCommand(persistentData);

        boolean success = listCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertEquals(Arrays.asList("=== Actors ===", "", "=== Worlds ==="), messages);
    }

    @Test
    public void testListShowsActorsUnderActorsHeadingAndWorldsUnderWorldsHeading() {
        testUtilities.createActor("Gerald");
        testUtilities.createWorld("Earth");
        ListCommand listCommand = new ListCommand(persistentData);

        boolean success = listCommand.execute(console);

        Assert.assertTrue(success);
        int actorsHeading = messages.indexOf("=== Actors ===");
        int worldsHeading = messages.indexOf("=== Worlds ===");
        int actorLine = messages.indexOf("Gerald");
        int worldLine = messages.indexOf("Earth");
        Assert.assertTrue(actorsHeading < actorLine && actorLine < worldsHeading);
        Assert.assertTrue(worldsHeading < worldLine);
    }

    @Test
    public void testListIgnoresArguments() {
        testUtilities.createActor("Gerald");
        ListCommand listCommand = new ListCommand(persistentData);

        boolean success = listCommand.execute(console, new String[]{"unexpected"});

        Assert.assertTrue(success);
        Assert.assertTrue(messages.contains("Gerald"));
    }
}
