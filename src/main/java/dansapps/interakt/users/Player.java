package dansapps.interakt.users;

import dansapps.interakt.users.abs.CommandSenderImpl;

/**
 * @author Daniel McCoy Stephenson
 * @since May 7th, 2022
 */
public class Player extends CommandSenderImpl {

    public Player() {
        addPermission("interakt.help");
    }
}
