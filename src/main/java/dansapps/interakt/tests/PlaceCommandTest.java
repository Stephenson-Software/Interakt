package dansapps.interakt.tests;

import dansapps.interakt.commands.console.PlaceCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

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
}