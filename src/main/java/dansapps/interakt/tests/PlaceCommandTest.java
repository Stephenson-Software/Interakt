package dansapps.interakt.tests;

import dansapps.interakt.commands.console.PlaceCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.users.Console;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
import org.junit.Assert;
import org.junit.Test;

public class PlaceCommandTest {

    @Test
    public void testPlaceCommand() {
        String worldName = "TestWorld";
        String actorName = "TestActor";

        TestUtilities.getInstance().createWorld(worldName);
        TestUtilities.getInstance().createActor(actorName);

        PlaceCommand placeCommand = new PlaceCommand();
        Console console = new Console();
        String[] args = new String[2];
        args[0] = TestUtilities.getInstance().wrapInQuotationMarks(actorName);
        args[1] = TestUtilities.getInstance().wrapInQuotationMarks(worldName);
        placeCommand.execute(console, args);

        World world = null;
        try {
            world = PersistentData.getInstance().getWorld(worldName);
        } catch (Exception e) {
            Assert.fail();
        }

        Actor actor = null;
        try {
            actor = PersistentData.getInstance().getActor(actorName);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(actor.getWorld().getUUID(), world.getUUID());
    }
}