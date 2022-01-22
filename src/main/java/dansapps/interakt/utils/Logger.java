/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.utils;

import dansapps.interakt.Interakt;
import dansapps.interakt.objects.Event;
import dansapps.interakt.services.LocalStorageService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Logger {
    private static Logger instance;
    private boolean localDebugFlag = false;
    private File logFile;
    private FileWriter fileWriter;

    private Logger() {
        String filePath = LocalStorageService.FILE_PATH + LocalStorageService.LOG_FILE_NAME;
        logFile = new File(filePath);
        try {
            logFile.createNewFile();
            fileWriter = new FileWriter(logFile.getName());
        }
        catch(Exception e) {
            Logger.getInstance().logError("Something went wrong instantiating the file writer for the Logger.");
        }

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
    public void logInfo(String message) {
        if (isLocalDebugFlagEnabled() || Interakt.getInstance().isDebugEnabled()) {
            System.out.println("[INFO] " + message);
        }
    }

    public void logError(String errorMessage) {
        if (Interakt.getInstance().isDebugEnabled()) {
            System.out.println("[ERROR] " + errorMessage);
        }
    }

    public void logEvent(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = event.getTimestamp().format(formatter);
        String message = "[" + formattedDateTime + "] " + event.getMessage();
        System.out.println(message);
        logToFile(message);
    }

    public static void setInstance(Logger instance) {
        Logger.instance = instance;
    }

    public boolean isLocalDebugFlagEnabled() {
        return localDebugFlag;
    }

    public void setLocalDebugFlagEnabled(boolean localDebugFlag) {
        this.localDebugFlag = localDebugFlag;
    }

    private void logToFile(String message) {
        try {
            fileWriter.append(message);
        }
        catch (IOException e) {
            logError("There was a problem logging a message to the log file.");
        }
    }
}