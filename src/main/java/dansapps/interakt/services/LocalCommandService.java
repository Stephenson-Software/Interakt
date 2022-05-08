package dansapps.interakt.services;

import dansapps.interakt.users.abs.CommandSenderImpl;
import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;
import preponderous.ponder.system.services.CommandService;

import java.util.HashSet;
import java.util.LinkedList;

public class LocalCommandService extends CommandService {
    private HashSet<ApplicationCommand> commands;
    private final LinkedList<String> commandHistory = new LinkedList<>();

    public LocalCommandService(HashSet<ApplicationCommand> commands) {
        super(commands);
        this.commands = commands;
    }

    /**
     * @param sender The sender of the command.
     * @param label The label of the command.
     * @param args The arguments of the command.
     */
    @Override
    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof CommandSenderImpl)) {
            return false;
        }

        CommandSenderImpl commandSender = (CommandSenderImpl) sender;

        for (ApplicationCommand command : commands) {
            if (commandCanBeInvokedWithLabel(command, label)) {

                if (!((CommandSenderImpl) sender).hasPermission(command.getPermissions().get(0))) {
                    commandSender.sendMessage("You don't have permission to use this command.");
                    return false;
                }
                return executeCommand(commandSender, command, args);
            }
        }
        commandSender.sendMessage("That command wasn't found.");
        return false;
    }

    /**
     * @param command The command to check.
     * @param label The label to use to check.
     * @return Whether the command can be invoked with the passed label.
     */
    private boolean commandCanBeInvokedWithLabel(ApplicationCommand command, String label) {
        return command.getNames().contains(label);
    }

    /**
     * @param sender The sender of the command.
     * @param command The command to invoke.
     * @param args The arguments of the command.
     * @return Whether the invocation of the command was successful.
     */
    private boolean executeCommand(CommandSender sender, ApplicationCommand command, String[] args) {
        if (args.length == 0) {
            return command.execute(sender);
        }
        else {
            return command.execute(sender, args);
        }
    }

    public void addCommand(String name) {
        commandHistory.add(name);
    }

    public void removeCommand(String name) {
        commandHistory.remove(name);
    }

    public boolean isCommandInHistory(String name) {
        return commandHistory.contains(name);
    }

    public String getMostRecentCommand() {
        return commandHistory.getLast();
    }
}