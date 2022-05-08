/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.users.abs;

import preponderous.ponder.system.abs.CommandSender;

import java.util.HashSet;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class CommandSenderImpl extends CommandSender {
    private HashSet<String> permissions = new HashSet<>();

    /**
     * This can be used to send a message to the command sender, who in this case is the user of the console.
     * @param message The message to send to the command sender.
     */
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    protected boolean addPermission(String permission) {
        return permissions.add(permission);
    }

    protected boolean removePermission(String permission) {
        return permissions.remove(permission);
    }
}
