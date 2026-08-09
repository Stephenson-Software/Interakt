package dansapps.interakt.tests;

import dansapps.interakt.commands.console.CreateCommand;
import dansapps.interakt.commands.console.DeleteCommand;
import dansapps.interakt.commands.console.ElapseCommand;
import dansapps.interakt.commands.console.GenerateTestDataCommand;
import dansapps.interakt.commands.console.InfoCommand;
import dansapps.interakt.commands.console.ListCommand;
import dansapps.interakt.commands.console.PlaceCommand;
import dansapps.interakt.commands.console.RelationsCommand;
import dansapps.interakt.commands.console.StatsCommand;
import dansapps.interakt.commands.console.ViewCommand;
import dansapps.interakt.commands.console.WipeCommand;
import dansapps.interakt.commands.multi.HelpCommand;
import dansapps.interakt.commands.multi.QuitCommand;
import dansapps.interakt.commands.multi.SaveCommand;
import dansapps.interakt.services.LocalCommandService;
import dansapps.interakt.users.Console;
import org.junit.Assert;
import org.junit.Test;
import preponderous.ponder.system.abs.ApplicationCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConsolePermissionsTest {

    /**
     * The commands a console user is expected to be able to invoke. These mirror the registrations in
     * Interakt#getCommands(). Their collaborators are only assigned by the constructors, so null is
     * sufficient here — no command is executed by the parity test, only its permission is read.
     */
    private List<ApplicationCommand> getConsoleCommands() {
        return List.of(
                new HelpCommand(),
                new InfoCommand(),
                new QuitCommand(null),
                new CreateCommand(null, null),
                new DeleteCommand(null),
                new ViewCommand(null),
                new ListCommand(null),
                new PlaceCommand(null),
                new StatsCommand(null, null),
                new WipeCommand(null),
                new ElapseCommand(null),
                new SaveCommand(null),
                new GenerateTestDataCommand(null, null, null),
                new RelationsCommand(null)
        );
    }

    @Test
    public void testConsoleHasPermissionForEveryConsoleCommand() {
        Console console = new Console();

        List<String> missingPermissions = new ArrayList<>();
        for (ApplicationCommand command : getConsoleCommands()) {
            String permission = command.getPermissions().get(0);
            if (!console.hasPermission(permission)) {
                missingPermissions.add(permission);
            }
        }

        Assert.assertEquals("Console is missing permissions: " + missingPermissions, 0, missingPermissions.size());
    }

    @Test
    public void testInfoCommandIsExecutableByConsole() {
        List<String> messages = new ArrayList<>();
        Console console = new Console() {
            @Override
            public void sendMessage(String message) {
                messages.add(message);
            }
        };

        HashSet<ApplicationCommand> commands = new HashSet<>();
        commands.add(new InfoCommand());
        LocalCommandService commandService = new LocalCommandService(commands);

        boolean success = commandService.interpretCommand(console, "info", new String[0]);

        Assert.assertTrue(success);
        Assert.assertTrue(messages.contains("=== Interakt Info ==="));
        Assert.assertFalse(messages.contains("You don't have permission to use this command."));
    }
}
