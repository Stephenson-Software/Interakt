package dansapps.interakt.tests;

import dansapps.interakt.commands.console.DeleteCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

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
}