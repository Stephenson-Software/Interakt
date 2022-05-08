package dansapps.interakt.users;

import dansapps.interakt.users.abs.CommandSenderImpl;

/**
 * @author Daniel McCoy Stephenson
 * @since May 7th, 2022
 */
public class Console extends CommandSenderImpl {

    public Console() {
        addPermission("interakt.create");
        addPermission("interakt.delete");
        addPermission("interakt.elapse");
        addPermission("interakt.generatetestdata");
        addPermission("interakt.help");
        addPermission("interakt.list");
        addPermission("interakt.place");
        addPermission("interakt.quit");
        addPermission("interakt.relations");
        addPermission("interakt.save");
        addPermission("interakt.stats");
        addPermission("interakt.view");
        addPermission("interakt.wipe");
    }
}
