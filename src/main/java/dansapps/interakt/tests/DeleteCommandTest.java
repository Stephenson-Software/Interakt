package dansapps.interakt.tests;

import dansapps.interakt.commands.console.DeleteCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

public class DeleteCommandTest {

    @Test
    public void testDeleteActor() {
        String name = "Gerald";
        TestUtilities.getInstance().createActor(name);

        DeleteCommand deleteCommand = new DeleteCommand();
        Console console = new Console();
        String[] args = new String[2];
        args[0] = TestUtilities.getInstance().wrapInQuotationMarks("actor");
        args[1] = TestUtilities.getInstance().wrapInQuotationMarks(name);
        deleteCommand.execute(console, args);

        try {
            PersistentData.getInstance().getActor(name);
            Assert.fail();
        } catch(Exception ignored) {

        }
    }

    @Test
    public void testDeleteWorld() {
        String name = "Test";
        TestUtilities.getInstance().createWorld(name);

        DeleteCommand deleteCommand = new DeleteCommand();
        Console console = new Console();
        String[] args = new String[2];
        args[0] = TestUtilities.getInstance().wrapInQuotationMarks("world");
        args[1] = TestUtilities.getInstance().wrapInQuotationMarks(name);
        deleteCommand.execute(console, args);

        try {
            PersistentData.getInstance().getWorld(name);
            Assert.fail();
        } catch(Exception ignored) {

        }
    }
}