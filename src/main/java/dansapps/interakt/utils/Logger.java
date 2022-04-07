/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.utils;

import dansapps.interakt.Interakt;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static dansapps.interakt.services.LocalStorageService.FILE_PATH;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Logger {
    private static Logger instance;
    private boolean localDebugFlag = false;
    private static String PATH = FILE_PATH + "log.txt";
    private File file = new File(PATH);

    private Logger() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
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
        writeToFile(message);
        if (isLocalDebugFlagEnabled() || Interakt.getInstance().isDebugEnabled()) {
            System.out.println("[INFO] " + message);
        }
    }

    public void logError(String errorMessage) {
        writeToFile(errorMessage);
        if (Interakt.getInstance().isDebugEnabled()) {
            System.out.println("[ERROR] " + errorMessage);
        }
    }

    public void logEvent(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = event.getTimestamp().format(formatter);
        String datemessage = "[" + formattedDateTime + "] " + event.getMessage();
        writeToFile(datemessage);
        if (CONFIG.LOG_EVENTS_TO_CONSOLE) {
            System.out.println(datemessage);
        }
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

    private void writeToFile(String toWrite) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(toWrite + "\n");
            out.close();
            fileWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}