package dansapps.interakt.utils;

import dansapps.interakt.Interakt;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Logger {
    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    /**
     * This can be used to send a debug message to the console.
     * @param message The message to log to the console.
     */
    public void log(String message) {
        if (Interakt.getInstance().isDebugEnabled()) {
            System.out.println("[DEBUG] " + message);
        }
    }
}