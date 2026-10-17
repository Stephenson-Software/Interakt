package dansapps.interakt.tests;

import dansapps.interakt.commands.multi.SaveCommand;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Characterization tests for SaveCommand: its label and permission, and that it saves through
 * the storage service it was given before confirming, with or without arguments.
 */
public class SaveCommandTest {
    private final List<String> events = new ArrayList<>();
    private final LocalStorageService storageService = new LocalStorageService(null, null, null, null, null, null, null, null, null) {
        @Override
        public void save() {
            events.add("save");
        }
    };
    private final SaveCommand saveCommand = new SaveCommand(storageService);
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            events.add("message: " + message);
        }
    };

    @Test
    public void testNamesAndPermission() {
        Assert.assertEquals(Collections.singletonList("save"), saveCommand.getNames());
        Assert.assertEquals(Collections.singletonList("interakt.save"), saveCommand.getPermissions());
    }

    @Test
    public void testExecuteSavesThenConfirms() {
        boolean success = saveCommand.execute(console);

        Assert.assertTrue(success);
        Assert.assertEquals(List.of("save", "message: Saved."), events);
    }

    @Test
    public void testArgumentsAreIgnored() {
        boolean success = saveCommand.execute(console, new String[]{"\"unexpected\""});

        Assert.assertTrue(success);
        Assert.assertEquals(List.of("save", "message: Saved."), events);
    }
}
