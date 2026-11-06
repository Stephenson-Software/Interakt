package dansapps.interakt.tests;

import dansapps.interakt.commands.console.PlaceCommand;
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
import java.util.UUID;

public class PlaceCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);

    @Test
    public void testPlaceCommand() {
        String worldName = "TestWorld";
        String actorName = "TestActor";

        testUtilities.createWorld(worldName);
        testUtilities.createActor(actorName);

        PlaceCommand placeCommand = new PlaceCommand(persistentData);
        Console console = new Console();
        String[] args = new String[2];
        args[0] = testUtilities.wrapInQuotationMarks(actorName);
        args[1] = testUtilities.wrapInQuotationMarks(worldName);
        placeCommand.execute(console, args);

        World world = null;
        try {
            world = persistentData.getWorld(worldName);
        } catch (Exception e) {
            Assert.fail();
        }

        Actor actor = null;
        try {
            actor = persistentData.getActor(actorName);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(actor.getWorld().getUUID(), world.getUUID());
    }

    private final List<String> messages = new ArrayList<>();
    private final Console recordingConsole = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };
    private final PlaceCommand recordedPlaceCommand = new PlaceCommand(persistentData);

    private String[] quoted(String actorName, String worldName) {
        return new String[]{testUtilities.wrapInQuotationMarks(actorName), testUtilities.wrapInQuotationMarks(worldName)};
    }

    @Test
    public void testMissingWorldIsReportedOnce() {
        testUtilities.createActor("Gerald");

        boolean success = recordedPlaceCommand.execute(recordingConsole, quoted("Gerald", "Nowhere"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That world wasn't found."), messages);
    }

    @Test
    public void testMissingActorIsRejected() {
        testUtilities.createWorld("Earth");

        boolean success = recordedPlaceCommand.execute(recordingConsole, quoted("Nobody", "Earth"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That actor wasn't found."), messages);
    }

    @Test
    public void testPartiallyQuotedArgumentsAreRejected() {
        testUtilities.createActor("Gerald");
        testUtilities.createWorld("Earth");

        boolean success = recordedPlaceCommand.execute(recordingConsole, new String[]{testUtilities.wrapInQuotationMarks("Gerald"), "Earth"});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Arguments must be designated in between quotation marks."), messages);
    }

    @Test
    public void testActorAlreadyInAWorldIsRejected() {
        testUtilities.createActor("Gerald");
        testUtilities.createWorld("Earth");
        testUtilities.createWorld("Mars");
        recordedPlaceCommand.execute(new Console(), quoted("Gerald", "Earth"));

        boolean success = recordedPlaceCommand.execute(recordingConsole, quoted("Gerald", "Mars"));

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("That actor is already in a world."), messages);
    }

    @Test
    public void testActorWhoseWorldNoLongerExistsCanBePlaced() throws Exception {
        testUtilities.createActor("Gerald");
        testUtilities.createWorld("Earth");
        Actor actor = persistentData.getActor("Gerald");
        actor.setEnvironmentUUID(UUID.randomUUID());

        boolean success = recordedPlaceCommand.execute(recordingConsole, quoted("Gerald", "Earth"));

        Assert.assertTrue(success);
        Assert.assertSame(persistentData.getWorld("Earth"), actor.getWorld());
    }
}
