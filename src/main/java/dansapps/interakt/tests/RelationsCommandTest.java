package dansapps.interakt.tests;

import dansapps.interakt.commands.console.RelationsCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.tests.utils.TestUtilities;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RelationsCommandTest {
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
    public void testNoArgumentsShowsUsage() {
        RelationsCommand relationsCommand = new RelationsCommand(persistentData);

        boolean success = relationsCommand.execute(console);

        Assert.assertFalse(success);
        Assert.assertTrue(messages.contains("Usage: relations \"actor\""));
    }

    @Test
    public void testUnquotedActorNameIsRejected() {
        testUtilities.createActor("Gerald");
        RelationsCommand relationsCommand = new RelationsCommand(persistentData);

        boolean success = relationsCommand.execute(console, new String[]{"Gerald"});

        Assert.assertFalse(success);
        Assert.assertTrue(messages.contains("Actor must be designated in between quotation marks."));
    }

    @Test
    public void testUnknownActorIsReported() {
        RelationsCommand relationsCommand = new RelationsCommand(persistentData);

        boolean success = relationsCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("Nobody")});

        Assert.assertFalse(success);
        Assert.assertTrue(messages.contains("That actor wasn't found."));
    }

    @Test
    public void testRelationsAreListedForKnownActor() throws Exception {
        testUtilities.createActor("Gerald");
        testUtilities.createActor("Hilda");
        Actor gerald = persistentData.getActor("Gerald");
        Actor hilda = persistentData.getActor("Hilda");
        gerald.setRelation(hilda, 42);
        RelationsCommand relationsCommand = new RelationsCommand(persistentData);

        boolean success = relationsCommand.execute(console, new String[]{testUtilities.wrapInQuotationMarks("Gerald")});

        Assert.assertTrue(success);
        Assert.assertEquals("=== Relations ===", messages.get(0));
        Assert.assertEquals("Hilda: 42\n", messages.get(1));
    }
}
