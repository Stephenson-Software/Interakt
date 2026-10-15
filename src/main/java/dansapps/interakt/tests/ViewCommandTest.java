package dansapps.interakt.tests;

import dansapps.interakt.commands.console.ViewCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Characterization tests for ViewCommand: usage and argument validation, viewing actors and
 * worlds by name, and the messages sent when the target or type is not found.
 */
public class ViewCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);
    private final ViewCommand viewCommand = new ViewCommand(persistentData);
    private final List<String> messages = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    private String[] quoted(String type, String name) {
        return new String[]{testUtilities.wrapInQuotationMarks(type), testUtilities.wrapInQuotationMarks(name)};
    }

    @Test
    public void testExecuteWithoutArgumentsSendsUsage() {
        boolean success = viewCommand.execute(console);

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Usage: view \"type\" \"name\""), messages);
    }

    @Test
    public void testTooFewArgumentsIsRejected() {
        boolean success = viewCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("actor")});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Not enough arguments."), messages);
    }

    @Test
    public void testUnquotedArgumentsAreRejected() {
        testUtilities.createActor("Gerald");

        boolean success = viewCommand.execute(console, new String[]{"actor", "Gerald"});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Arguments must be designated in between quotation marks."), messages);
    }

    @Test
    public void testViewActorSendsActorDetails() throws Exception {
        testUtilities.createActor("Gerald");
        Actor actor = persistentData.getActor("Gerald");

        boolean success = viewCommand.execute(console, quoted("actor", "Gerald"));

        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList(actor.toString()), messages);
        Assert.assertTrue(messages.get(0).startsWith("=== Details of Gerald ==="));
    }

    @Test
    public void testViewTypeIsCaseInsensitive() {
        testUtilities.createActor("Gerald");

        boolean success = viewCommand.execute(console, quoted("ACTOR", "Gerald"));

        Assert.assertTrue(success);
        Assert.assertTrue(messages.get(0).startsWith("=== Details of Gerald ==="));
    }

    @Test
    public void testViewMissingActorIsRejected() {
        boolean success = viewCommand.execute(console, quoted("actor", "Nobody"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That actor wasn't found."), messages);
    }

    @Test
    public void testViewWorldSendsWorldDetails() throws Exception {
        testUtilities.createWorld("Earth");
        World world = persistentData.getWorld("Earth");

        boolean success = viewCommand.execute(console, quoted("world", "Earth"));

        Assert.assertTrue(success);
        Assert.assertEquals("=== Details of Earth ===", messages.get(0));
        Assert.assertEquals("UUID: " + world.getUUID(), messages.get(1));
        Assert.assertTrue(messages.get(messages.size() - 1).startsWith("Grid:\n"));
    }

    @Test
    public void testViewMissingWorldIsRejected() {
        boolean success = viewCommand.execute(console, quoted("world", "Nowhere"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That world wasn't found."), messages);
    }

    @Test
    public void testUnsupportedTypeIsRejected() {
        testUtilities.createActor("Gerald");

        boolean success = viewCommand.execute(console, quoted("square", "Gerald"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That type isn't supported. Supported types include actor and world."), messages);
    }

    @Test
    public void testPartiallyQuotedArgumentsAreRejected() {
        testUtilities.createActor("Gerald");

        boolean success = viewCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("actor"), "Gerald"});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Arguments must be designated in between quotation marks."), messages);
    }
}
