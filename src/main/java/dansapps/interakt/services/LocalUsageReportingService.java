/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt.services;

import dansapps.interakt.Interakt;
import dansapps.interakt.trace.TraceClient;
import dansapps.interakt.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;

/**
 * Reports that Interakt was started to the trace service, so that it is known whether anybody runs
 * the program. Exactly one event is sent per start: its name is {@code startup} and its only tag is
 * the program version. No usernames, hostnames, paths, actor names or world names are ever sent.
 * <p>
 * The report is handed to {@link TraceClient}, which sends it from a daemon thread, never throws,
 * and drops the report rather than waiting if the service cannot be reached. Reporting is on by
 * default and is switched off in {@value #SETTINGS_FILE_NAME}, which lives alongside the
 * application's data files and is written with its defaults the first time the application
 * starts, or with the environment variables every trace client honours,
 * {@code TRACE_USAGE_REPORTING=off} and {@code DO_NOT_TRACK=1}, which the client checks before
 * the settings file. Details: {@value #DETAILS_URL}
 *
 * @author Daniel McCoy Stephenson
 * @since September 11th, 2026
 */
public class LocalUsageReportingService {
    public static final String SETTINGS_FILE_NAME = "usage-reporting.properties";
    public static final String ENABLED_KEY = "usage_reporting.enabled";
    public static final String ENDPOINT_KEY = "usage_reporting.endpoint";
    public static final String KEY_KEY = "usage_reporting.key";
    public static final String DEFAULT_ENDPOINT = "https://trace.danielstephenson.dev";
    /** The write key issued to Interakt by the trace service. It can only add usage events. */
    public static final String DEFAULT_KEY = "lguRpjWpYkg9ESt5Q0YBcf4X-xih9inROo46l-vPsrA";
    public static final String APPLICATION_NAME = "Interakt";
    /** The public page describing what trace collects and every way to turn it off. */
    public static final String DETAILS_URL = "https://github.com/Stephenson-Software/trace#usage-reporting";
    private static final String VERSION_RESOURCE = "/interakt.properties";

    private final File settingsFile;
    private final Logger logger;
    private final PrintStream out;
    private final String defaultEndpoint;
    private TraceClient traceClient = TraceClient.disabled();

    /**
     * @param settingsDirectory The directory the settings file is read from and, on the first run,
     *                          written to.
     * @param logger The application logger, used for debug output only.
     * @param out Where the one-time first-run notice is printed (only when reporting is actually
     *            on). This is the console the user is looking at, so System.out in the running
     *            application.
     */
    public LocalUsageReportingService(File settingsDirectory, Logger logger, PrintStream out) {
        this(settingsDirectory, logger, out, DEFAULT_ENDPOINT);
    }

    /**
     * As above, but with the endpoint that applies (and is written to the settings file) when the
     * settings do not exist yet. Tests use this to keep a first run away from the real service.
     */
    public LocalUsageReportingService(File settingsDirectory, Logger logger, PrintStream out, String defaultEndpoint) {
        this.settingsFile = new File(settingsDirectory, SETTINGS_FILE_NAME);
        this.logger = logger;
        this.out = out;
        this.defaultEndpoint = defaultEndpoint;
    }

    /**
     * Reads the settings (writing them with their defaults if they do not exist yet), builds the
     * client accordingly, prints the first-run notice if this is a first run and reporting is
     * actually on, and reports the startup event. Nothing
     * in here can stop the application from starting: a settings file that cannot be read or
     * written leaves reporting at its defaults, and the report itself is sent off the calling
     * thread by the client.
     */
    public void start() {
        boolean firstRun = !settingsFile.exists();
        Properties settings = readSettings();
        String endpoint = settings.getProperty(ENDPOINT_KEY, defaultEndpoint);
        traceClient = TraceClient.builder(endpoint, APPLICATION_NAME)
                .key(settings.getProperty(KEY_KEY, DEFAULT_KEY))
                .enabled(Boolean.parseBoolean(settings.getProperty(ENABLED_KEY, "true")))
                .build();
        if (firstRun && traceClient.isEnabled()) {
            // Printed after the client is built so that an environment that has already turned
            // reporting off (TRACE_USAGE_REPORTING / DO_NOT_TRACK) is never told it is on.
            out.println(getFirstRunNotice());
        }
        if (traceClient.isEnabled()) {
            logger.logInfo("Reporting startup to " + endpoint);
            traceClient.report("startup", null, Collections.singletonMap("version", getVersion()));
        }
        else {
            logger.logInfo("Usage reporting is off (" + getDisabledReason() + "). Details: " + DETAILS_URL);
        }
    }

    /**
     * @return Why nothing is sent, in Interakt's own terms, or null while reporting is on. The
     *         client's reasons are worded for a Spigot plugin, so its "config.yml" becomes the
     *         settings file a user of Interakt actually edits.
     */
    public String getDisabledReason() {
        String reason = traceClient.disabledReason();
        if (reason == null) {
            return null;
        }
        if (TraceClient.REASON_ENVIRONMENT.equals(reason)) {
            return "environment: " + TraceClient.ENV_USAGE_REPORTING + " or " + TraceClient.ENV_DO_NOT_TRACK;
        }
        if (TraceClient.REASON_CONFIG.equals(reason)) {
            return ENABLED_KEY + "=false in " + settingsFile.getPath();
        }
        return reason;
    }

    /**
     * Stops the client's sending thread. A report still in flight is given a moment to finish.
     */
    public void close() {
        traceClient.close();
    }

    /**
     * @return Whether the startup event is actually being sent, i.e. reporting is enabled and a key
     *         is present.
     */
    public boolean isEnabled() {
        return traceClient.isEnabled();
    }

    /**
     * @return The endpoint used, and written to the settings file, when the settings do not exist.
     */
    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    /**
     * @return The file the settings are read from.
     */
    public File getSettingsFile() {
        return settingsFile;
    }

    /**
     * The text a user sees once, the first time the application starts after this feature was
     * added, telling them what is sent and how to turn it off.
     */
    public String getFirstRunNotice() {
        return "Usage reporting is on: " + APPLICATION_NAME + " sends its name and version (one startup event) to "
                + "https://trace.danielstephenson.dev - nothing about you, your actors, your worlds or this machine. "
                + "Turn it off with " + ENABLED_KEY + "=false in " + settingsFile.getPath()
                + ", or with TRACE_USAGE_REPORTING=off in the environment. Details: " + DETAILS_URL;
    }

    /**
     * Interakt's own version, as baked into the jar from the pom at build time.
     * @return The version, or "unknown" when the application is run from somewhere the resource
     *         cannot be found.
     */
    public static String getVersion() {
        Properties buildInfo = new Properties();
        try (InputStream in = Interakt.class.getResourceAsStream(VERSION_RESOURCE)) {
            if (in != null) {
                buildInfo.load(in);
            }
        } catch (IOException e) {
            // fall through to the default
        }
        String version = buildInfo.getProperty("version", "").trim();
        if (version.isEmpty() || version.startsWith("${")) {
            return "unknown";
        }
        return version;
    }

    private Properties readSettings() {
        Properties settings = new Properties();
        if (!settingsFile.exists()) {
            writeDefaultSettings();
            return settings;
        }
        try (InputStream in = new FileInputStream(settingsFile)) {
            settings.load(in);
        } catch (IOException e) {
            logger.logError("Could not read " + settingsFile.getPath() + ", so usage reporting keeps its defaults: " + e);
        }
        return settings;
    }

    private void writeDefaultSettings() {
        File directory = settingsFile.getParentFile();
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            logger.logError("Could not create " + directory.getPath() + " for " + SETTINGS_FILE_NAME + ".");
            return;
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(settingsFile), StandardCharsets.UTF_8)) {
            writer.write("# Usage reporting for " + APPLICATION_NAME + ".\n");
            writer.write("# When enabled, one 'startup' event carrying only the program name and version is sent\n");
            writer.write("# to the endpoint each time the application starts. Nothing else is ever sent.\n");
            writer.write("# Set " + ENABLED_KEY + " to false to turn it off. TRACE_USAGE_REPORTING=off or\n");
            writer.write("# DO_NOT_TRACK=1 in the environment turns it off too, whatever this file says.\n");
            writer.write("# Details: " + DETAILS_URL + "\n");
            writer.write(ENABLED_KEY + "=true\n");
            writer.write(ENDPOINT_KEY + "=" + defaultEndpoint + "\n");
            writer.write(KEY_KEY + "=" + DEFAULT_KEY + "\n");
        } catch (IOException e) {
            logger.logError("Could not write " + settingsFile.getPath() + ": " + e);
        }
    }
}
