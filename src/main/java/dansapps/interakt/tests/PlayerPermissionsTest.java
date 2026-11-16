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
import dansapps.interakt.users.Player;
import org.junit.Assert;
import org.junit.Test;
import preponderous.ponder.system.abs.ApplicationCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Characterization tests for the permissions of a Player: a player may use exactly the commands
 * HelpCommand advertises to players (help, save and quit) and is refused every console-only command
 * before the command runs. The command collaborators are null because a refused command is never
 * executed and the permission parity checks only read each command's permission.
 */
public class PlayerPermissionsTest {
    private final List<String> messages = new ArrayList<>();
    private final Player player = new Player() {
        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }
    };

    private List<ApplicationCommand> getPlayerCommands() {
        return List.of(
                new HelpCommand(),
                new SaveCommand(null),
                new QuitCommand(null)
        );
    }

    private List<ApplicationCommand> getConsoleOnlyCommands() {
        return List.of(
                new InfoCommand(),
                new CreateCommand(null, null),
                new DeleteCommand(null),
                new ViewCommand(null),
                new ListCommand(null),
                new PlaceCommand(null),
                new StatsCommand(null, null),
                new WipeCommand(null),
                new ElapseCommand(null),
                new GenerateTestDataCommand(null, null, null),
                new RelationsCommand(null)
        );
    }

    @Test
    public void testPlayerHasPermissionForEveryPlayerCommand() {
        for (ApplicationCommand command : getPlayerCommands()) {
            String permission = command.getPermissions().get(0);
            Assert.assertTrue("Player is missing " + permission, player.hasPermission(permission));
        }
    }

    @Test
    public void testPlayerHasNoPermissionForAnyConsoleOnlyCommand() {
        List<String> grantedPermissions = new ArrayList<>();
        for (ApplicationCommand command : getConsoleOnlyCommands()) {
            String permission = command.getPermissions().get(0);
            if (player.hasPermission(permission)) {
                grantedPermissions.add(permission);
            }
        }

        Assert.assertEquals("Player was granted console-only permissions: " + grantedPermissions, 0, grantedPermissions.size());
    }

    @Test
    public void testUnknownPermissionIsGrantedToNeitherUser() {
        Assert.assertFalse(player.hasPermission("interakt.sentinel"));
        Assert.assertFalse(new Console().hasPermission("interakt.sentinel"));
    }

    @Test
    public void testEveryCommandAdvertisedToPlayersIsPermittedForPlayers() {
        new HelpCommand().execute(player);

        Assert.assertEquals("=== Player Commands ===", messages.get(0));
        List<String> advertisedNames = new ArrayList<>();
        for (String message : messages.subList(1, messages.size())) {
            advertisedNames.add(message.substring(0, message.indexOf(" - ")));
        }
        Assert.assertEquals(List.of("help", "save", "quit"), advertisedNames);
        for (String name : advertisedNames) {
            Assert.assertTrue("Player is missing interakt." + name, player.hasPermission("interakt." + name));
        }
    }

    @Test
    public void testConsoleOnlyCommandIsRefusedBeforeItRuns() {
        HashSet<ApplicationCommand> commands = new HashSet<>();
        commands.add(new WipeCommand(null));
        LocalCommandService commandService = new LocalCommandService(commands);

        boolean success = commandService.interpretCommand(player, "wipe", new String[0]);

        Assert.assertFalse(success);
        Assert.assertEquals(List.of("You don't have permission to use this command."), messages);
    }

    @Test
    public void testHelpCommandIsExecutableByPlayer() {
        HashSet<ApplicationCommand> commands = new HashSet<>();
        commands.add(new HelpCommand());
        LocalCommandService commandService = new LocalCommandService(commands);

        boolean success = commandService.interpretCommand(player, "help", new String[0]);

        Assert.assertTrue(success);
        Assert.assertTrue(messages.contains("=== Player Commands ==="));
        Assert.assertFalse(messages.contains("You don't have permission to use this command."));
    }
}
