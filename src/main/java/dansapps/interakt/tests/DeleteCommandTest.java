package dansapps.interakt.tests;

import dansapps.interakt.commands.console.DeleteCommand;
import dansapps.interakt.commands.console.PlaceCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeleteCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);

    @Test
    public void testDeleteActor() {
        String name = "Gerald";
        testUtilities.createActor(name);

        DeleteCommand deleteCommand = new DeleteCommand(persistentData);
        Console console = new Console();
        String[] args = new String[2];
        args[0] = testUtilities.wrapInQuotationMarks("actor");
        args[1] = testUtilities.wrapInQuotationMarks(name);
        deleteCommand.execute(console, args);

        try {
            persistentData.getActor(name);
            Assert.fail();
        } catch(Exception ignored) {

        }
    }

    @Test
    public void testDeleteWorld() {
        String name = "Test";
        testUtilities.createWorld(name);

        DeleteCommand deleteCommand = new DeleteCommand(persistentData);
        Console console = new Console();
        String[] args = new String[2];
        args[0] = testUtilities.wrapInQuotationMarks("world");
        args[1] = testUtilities.wrapInQuotationMarks(name);
        deleteCommand.execute(console, args);

        try {
            persistentData.getWorld(name);
            Assert.fail();
        } catch(Exception ignored) {

        }
    }

    @Test
    public void testPartiallyQuotedArgumentsAreRejected() {
        String name = "Gerald";
        testUtilities.createActor(name);
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };

        DeleteCommand deleteCommand = new DeleteCommand(persistentData);
        boolean success = deleteCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("actor"), name});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Arguments must be designated in between quotation marks."), messages);
        Assert.assertEquals(1, persistentData.getActors().size());
    }

    @Test
    public void testActorsOfADeletedWorldCanBeViewedAndPlacedElsewhere() throws Exception {
        testUtilities.createWorld("Earth");
        testUtilities.createWorld("Mars");
        testUtilities.createActor("Gerald");
        Actor actor = persistentData.getActor("Gerald");
        Console console = new Console();
        new PlaceCommand(persistentData).execute(console, new String[]{testUtilities.wrapInQuotationMarks("Gerald"), testUtilities.wrapInQuotationMarks("Earth")});

        boolean deleted = new DeleteCommand(persistentData).execute(console, new String[]{testUtilities.wrapInQuotationMarks("world"), testUtilities.wrapInQuotationMarks("Earth")});

        Assert.assertTrue(deleted);
        Assert.assertTrue(actor.toString().contains("World: N/A"));
        boolean placed = new PlaceCommand(persistentData).execute(console, new String[]{testUtilities.wrapInQuotationMarks("Gerald"), testUtilities.wrapInQuotationMarks("Mars")});
        Assert.assertTrue(placed);
        Assert.assertSame(persistentData.getWorld("Mars"), actor.getWorld());
    }
}
