package dansapps.interakt.tests;

import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateCommandTest {
    private final PersistentData persistentData = new PersistentData();
    private final TestUtilities testUtilities = new TestUtilities(persistentData);

    @Test
    public void testCreateActor() {
        String name = "Gerald";
        testUtilities.createActor(name);
        try {
            persistentData.getActor(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateWorld() {
        String name = "Test";
        testUtilities.createWorld(name);
        try {
            persistentData.getWorld(name);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testPartiallyQuotedArgumentsAreRejected() {
        CreateCommand createCommand = testUtilities.createCreateCommand();
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };

        boolean success = createCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("actor"), "Gerald"});

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("Arguments must be designated in between quotation marks."), messages);
        Assert.assertTrue(persistentData.getActors().isEmpty());
    }

}
