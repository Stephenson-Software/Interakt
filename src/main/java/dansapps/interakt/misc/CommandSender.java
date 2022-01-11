/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.misc;

import preponderous.ponder.system.abs.AbstractCommandSender;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class CommandSender extends AbstractCommandSender {

    /**
     * This can be used to send a message to the command sender, who in this case is the user of the console.
     * @param message The message to send to the command sender.
     */
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
}
