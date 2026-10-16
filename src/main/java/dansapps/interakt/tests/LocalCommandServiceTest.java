package dansapps.interakt.tests;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.services.LocalCommandService;
import dansapps.interakt.users.Console;
import dansapps.interakt.users.Player;
import org.junit.Assert;
import org.junit.Test;
import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Characterization tests for LocalCommandService: dispatching a label to the matching command,
 * the permission check, the messages sent for unknown commands and missing permissions, and the
 * command history.
 */
public class LocalCommandServiceTest {
    private final List<String> messages = new ArrayList<>();
    private final List<String> invocations = new ArrayList<>();
    private final Console console = new Console() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    /**
     * A command that records how it was invoked instead of doing anything, and returns the given result.
     */
    private InteraktCommand createRecordingCommand(String permission, boolean result, String... names) {
        return new InteraktCommand(new ArrayList<>(List.of(names)), new ArrayList<>(List.of(permission))) {
            @Override
            public boolean execute(CommandSender sender) {
                invocations.add(names[0] + "()");
                return result;
            }

            @Override
            public boolean execute(CommandSender sender, String[] args) {
                invocations.add(names[0] + "(" + String.join(",", args) + ")");
                return result;
            }
        };
    }

    private LocalCommandService createService(ApplicationCommand... commands) {
        return new LocalCommandService(new HashSet<>(List.of(commands)));
    }

    @Test
    public void testLabelWithoutArgumentsInvokesNoArgumentExecute() {
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(console, "info", new String[0]);

        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList("info()"), invocations);
        Assert.assertTrue(messages.isEmpty());
    }

    @Test
    public void testLabelWithArgumentsPassesArgumentsToCommand() {
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(console, "info", new String[]{"a", "b"});

        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList("info(a,b)"), invocations);
    }

    @Test
    public void testAliasLabelInvokesCommand() {
        LocalCommandService service = createService(createRecordingCommand("interakt.quit", true, "quit", "exit"));

        boolean success = service.interpretCommand(console, "exit", new String[0]);

        Assert.assertTrue(success);
        Assert.assertEquals(Collections.singletonList("quit()"), invocations);
    }

    @Test
    public void testOnlyTheMatchingCommandIsInvoked() {
        LocalCommandService service = createService(
                createRecordingCommand("interakt.info", true, "info"),
                createRecordingCommand("interakt.list", true, "list"));

        service.interpretCommand(console, "list", new String[0]);

        Assert.assertEquals(Collections.singletonList("list()"), invocations);
    }

    @Test
    public void testCommandResultIsReturned() {
        LocalCommandService service = createService(createRecordingCommand("interakt.info", false, "info"));

        boolean success = service.interpretCommand(console, "info", new String[0]);

        Assert.assertFalse(success);
        Assert.assertEquals(Collections.singletonList("info()"), invocations);
    }

    @Test
    public void testLabelsAreCaseSensitive() {
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(console, "INFO", new String[0]);

        Assert.assertFalse(success);
        Assert.assertTrue(invocations.isEmpty());
        Assert.assertEquals(Collections.singletonList("That command wasn't found."), messages);
    }

    @Test
    public void testUnknownLabelIsRejected() {
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(console, "fly", new String[0]);

        Assert.assertFalse(success);
        Assert.assertTrue(invocations.isEmpty());
        Assert.assertEquals(Collections.singletonList("That command wasn't found."), messages);
    }

    @Test
    public void testSenderWithoutPermissionIsRejected() {
        Player player = new Player() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(player, "info", new String[0]);

        Assert.assertFalse(success);
        Assert.assertTrue(invocations.isEmpty());
        Assert.assertEquals(Collections.singletonList("You don't have permission to use this command."), messages);
    }

    @Test
    public void testSenderThatIsNotACommandSenderImplIsRejectedSilently() {
        CommandSender foreignSender = new CommandSender() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };
        LocalCommandService service = createService(createRecordingCommand("interakt.info", true, "info"));

        boolean success = service.interpretCommand(foreignSender, "info", new String[0]);

        Assert.assertFalse(success);
        Assert.assertTrue(invocations.isEmpty());
        Assert.assertTrue(messages.isEmpty());
    }

    @Test
    public void testCommandHistoryTracksAddedCommands() {
        LocalCommandService service = createService();

        service.addCommand("help");
        service.addCommand("list");

        Assert.assertTrue(service.isCommandInHistory("help"));
        Assert.assertTrue(service.isCommandInHistory("list"));
        Assert.assertFalse(service.isCommandInHistory("wipe"));
        Assert.assertEquals("list", service.getMostRecentCommand());
    }

    @Test
    public void testRemoveCommandRemovesOnlyTheFirstOccurrence() {
        LocalCommandService service = createService();
        service.addCommand("help");
        service.addCommand("list");
        service.addCommand("help");

        service.removeCommand("help");

        Assert.assertTrue(service.isCommandInHistory("help"));
        Assert.assertEquals("help", service.getMostRecentCommand());

        service.removeCommand("help");

        Assert.assertFalse(service.isCommandInHistory("help"));
        Assert.assertEquals("list", service.getMostRecentCommand());
    }

    @Test(expected = NoSuchElementException.class)
    public void testMostRecentCommandOnEmptyHistoryThrows() {
        createService().getMostRecentCommand();
    }
}
