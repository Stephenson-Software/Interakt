package dansapps.interakt.services;

import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.services.CommandService;

import java.util.HashSet;
import java.util.LinkedList;

public class LocalCommandService extends CommandService {
    private final LinkedList<String> commandHistory = new LinkedList<>();

    public LocalCommandService(HashSet<ApplicationCommand> commands) {
        super(commands);
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