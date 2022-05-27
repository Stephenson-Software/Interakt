package dansapps.interakt.commands.console;

import dansapps.interakt.commands.abs.InteraktCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.exceptions.NameTakenException;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.WorldFactory;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.World;
import preponderous.ponder.system.abs.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateTestDataCommand extends InteraktCommand {
    private WorldFactory worldFactory;
    private ActorFactory actorFactory;

    private static final ArrayList<String> names = new ArrayList<>(Arrays.asList(
            "Adam", "Bob", "Carl", "Damien", "Eve",
            "Frank", "Garry", "Harold", "Ishmael", "Josh",
            "Konrad", "Leonard", "Marion", "Noelle", "Odval",
            "Paul", "Quinn", "Randall", "Stephen", "Timmy",
            "Urg", "Vector", "Walter", "Xander", "Yamir",
            "Zeke"
    ));

    public GenerateTestDataCommand(WorldFactory worldFactory, ActorFactory actorFactory) {
        super(new ArrayList<>(List.of("generatetestdata", "gtd")), new ArrayList<>(List.of("interakt.generatetestdata")));
        this.worldFactory = worldFactory;
        this.actorFactory = actorFactory;
    }

    @Override
    public boolean execute(CommandSender sender) {
        worldFactory.createWorld("Test");
        World world;
        try {
            world = PersistentData.getInstance().getWorld("Test");
        } catch (Exception e) {
            sender.sendMessage("There was a problem accessing the test world after it was generated.");
            return false;
        }
        for (String name : names) {
            try {
                actorFactory.createActorWithName(name);
            } catch (NameTakenException e) {
                continue;
            }
            Actor actor;
            try {
                actor = PersistentData.getInstance().getActor(name);
            }
            catch (Exception e) {
                sender.sendMessage("There was a problem accessing an actor after it was generated.");
                continue;
            }
            if (actor.getWorld() != null) {
                sender.sendMessage("Test data has already been generated.");
                return false;
            }
            PersistentData.getInstance().placeIntoEnvironment(world, sender, actor);
        }
        sender.sendMessage("Test data has been generated.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}