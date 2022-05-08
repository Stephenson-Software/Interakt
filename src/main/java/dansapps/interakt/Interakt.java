/*
  Copyright (c) 2022 Daniel McCoy Stephenson
  Apache License 2.0
 */
package dansapps.interakt;

import dansapps.interakt.commands.console.*;
import dansapps.interakt.commands.multi.HelpCommand;
import dansapps.interakt.commands.multi.QuitCommand;
import dansapps.interakt.commands.multi.SaveCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
import dansapps.interakt.users.Console;
import dansapps.interakt.users.Player;
import dansapps.interakt.users.abs.CommandSenderImpl;
import dansapps.interakt.services.LocalAutoSaveService;
import dansapps.interakt.services.LocalCommandService;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.services.LocalTimeService;
import dansapps.interakt.utils.Logger;
import preponderous.environmentlib.EnvironmentLib;
import preponderous.ponder.system.abs.ApplicationCommand;
import preponderous.ponder.system.abs.CommandSender;
import preponderous.ponder.system.abs.PonderApplication;

import java.util.HashSet;
import java.util.Scanner;

/**
 * @author Daniel McCoy Stephenson
 * @since January 7th, 2022
 */
public class Interakt extends PonderApplication {
    private static Interakt instance;
    private boolean running = true;
    private LocalCommandService commandService;
    private final Scanner scanner = new Scanner(System.in);
    private final EnvironmentLib environmentLib = new EnvironmentLib();
    private CommandSenderImpl commandSender;
    private String playerActorName = "";

    /**
     * This can be utilized to access the self-managed instance of the application.
     * @return The self-managed instance of the application.
     */
    public static Interakt getInstance() {
        return instance;
    }

    /**
     * Initializes values and calls the onStartup method.
     */
    public Interakt() {
        super("Interakt", "This application is intended to allow the user to create and manage environments and entities that can exist within those environments.");
        onStartup();
    }

    /**
     * The primary method for the application. This is where the running loop can be found.
     * @param user The user of the application.
     */
    public void run(CommandSenderImpl user) {
        Logger.getInstance().logInfo("Running application.");
        Logger.getInstance().logInfo("Using EnvironmentLib " + environmentLib.getVersion());

        commandSender = user;

        String line;
        String label;
        String[] args;

        if (user instanceof Console) {
            user.sendMessage("Welcome to the Interakt console. Type help to see a list of useful commands.");
        }
        else if (user instanceof Player) {
            user.sendMessage("What is the name of your actor?");
            playerActorName = getScanner().nextLine();
            if (!PersistentData.getInstance().isActor(playerActorName)) {
                // create actor and place into test world
                Actor actor = new Actor(playerActorName);
                PersistentData.getInstance().addActor(actor);
                World world;
                try {
                    world = PersistentData.getInstance().getWorld("test");
                    PersistentData.getInstance().placeIntoEnvironment(world, user, actor);
                    user.sendMessage("You enter the world " + world.getName() + ".");
                } catch (Exception e) {
                    user.sendMessage("World 'test' needs to exist. Please generate test data via the console and try again.");
                    return;
                }
            }
            else {
                user.sendMessage("You are " + playerActorName + ".");
            }
        }

        while (isRunning()) {
            line = getInput();
            if (line == null) {
                return;
            }

            int indexOfFirstSpace = line.indexOf(' ');
            if (indexOfFirstSpace != -1) {
                label = line.substring(0, indexOfFirstSpace);
                args = line.substring(indexOfFirstSpace).split(" ");
            }
            else {
                label = line;
                args = new String[0];
            }

            getCommandService().addCommand(label);

            boolean success = onCommand(user, label, args);
            if (!success) {
                Logger.getInstance().logInfo("Something went wrong processing the " + label + " command.");
            }
        }
    }

    public String getPlayerActorName() {
        return playerActorName;
    }

    public CommandSenderImpl getCommandSender() {
        return commandSender;
    }

    /**
     * This can be used to get input from the console.
     * @return The inputted string.
     */
    private String getInput() {
        if (!getScanner().hasNext()) {
            return null;
        }
        return getScanner().nextLine();
    }

    /**
     * This will be called when the application starts up.
     */
    @Override
    public void onStartup() {
        instance = this;
        Logger.getInstance().logInfo("Initiating startup.");
        initializeCommandService();
        LocalStorageService.getInstance().load();
        LocalTimeService.getInstance().start();
        LocalAutoSaveService.getInstance().start();
    }

    /**
     * This will be called when the application shuts down.
     */
    @Override
    public void onShutdown() {
        Logger.getInstance().logInfo("Initiating shutdown.");
        LocalStorageService.getInstance().save();
    }

    /**
     * This will be called when a command is entered into the application.
     * @param sender The sender of the command.
     * @param label The label of the command.
     * @param args The arguments of the command.
     * @return Whether the execution of command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        Logger.getInstance().logInfo("Interpreting command " + label);
        return getCommandService().interpretCommand(sender, label, args);
    }

    /**
     * This can be used to check whether the debug flag is enabled.
     * @return Whether the debug flag is enabled.
     */
    public boolean isDebugEnabled() {
        return CONFIG.DEBUG_FLAG;
    }

    /**
     * This can be used to set the debug flag to true or false.
     * @param debug Desired value for the debug flag.
     */
    public void setDebugEnabled(boolean debug) {
        CONFIG.DEBUG_FLAG = debug;
    }

    /**
     * This can be used to check if the application is running.
     * @return Whether the application is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * This can be used to set the running flag of the application.
     * @param running Desired value for the running flag.
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return The instance of EnvironmentLib managed by this application.
     */
    public EnvironmentLib getEnvironmentLib() {
        return environmentLib;
    }

    /**
     * Initializes the command service with the application's commands.
     */
    private void initializeCommandService() {
        HashSet<ApplicationCommand> commands = new HashSet<>();
        commands.add(new HelpCommand());
        commands.add(new InfoCommand());
        commands.add(new QuitCommand());
        commands.add(new CreateCommand());
        commands.add(new DeleteCommand());
        commands.add(new ViewCommand());
        commands.add(new ListCommand());
        commands.add(new PlaceCommand());
        commands.add(new StatsCommand());
        commands.add(new WipeCommand());
        commands.add(new ElapseCommand());
        commands.add(new SaveCommand());
        commands.add(new GenerateTestDataCommand());
        commands.add(new RelationsCommand());
        setCommandService(new LocalCommandService(commands));
    }

    /**
     * This can be used to access Ponder's command service.
     * @return This application's managed instance of Ponder's command service.
     */
    private LocalCommandService getCommandService() {
        return commandService;
    }

    /**
     * This can be used to set this application's managed instance of Ponder's command service.
     * @param commandService The instance of Ponder's command service to use.
     */
    private void setCommandService(LocalCommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * This can be used to access this application's managed scanner.
     * @return This application's managed scanner.
     */
    private Scanner getScanner() {
        return scanner;
    }

    /**
     * Shuts down the application.
     */
    public void shutdownApplication() {
        Interakt.getInstance().onShutdown();
        System.exit(0);
    }

    /**
     * Instantiates and runs the application.
     * @param args The arguments given to the program.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing required argument: <console|player>");
            return;
        }
        String mode = args[0];

        Interakt application = new Interakt();

        if (mode.equalsIgnoreCase("console")) {
            Console console = new Console();
            application.run(console);
        }
        else if (mode.equalsIgnoreCase("player")) {
            Player player = new Player();
            application.run(player);
        }
        else {
            System.out.println("Unsupported mode entered. Supported modes: console, player");
        }
    }
}