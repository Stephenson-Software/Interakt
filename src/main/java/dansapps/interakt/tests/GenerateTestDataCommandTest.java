package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.actions.AttackAction;
import dansapps.interakt.actions.BefriendAction;
import dansapps.interakt.actions.MoveAction;
import dansapps.interakt.actions.ReproduceAction;
import dansapps.interakt.commands.console.GenerateTestDataCommand;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.*;
import dansapps.interakt.users.Console;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

public class GenerateTestDataCommandTest {
    private WorldFactory worldFactory = new WorldFactory();
    private EventFactory eventFactory = new EventFactory();
    private Interakt interakt = new Interakt();
    private Logger logger = new Logger(interakt);
    private EntityRecordFactory entityRecordFactory = new EntityRecordFactory(logger);
    private ActionRecordFactory actionRecordFactory = new ActionRecordFactory();
    private AttackAction attackAction = new AttackAction(eventFactory, logger, interakt, actionRecordFactory);
    private BefriendAction befriendAction = new BefriendAction(eventFactory, logger, interakt, actionRecordFactory);
    private MoveAction moveAction = new MoveAction(logger, eventFactory, interakt, actionRecordFactory);
    private ActorFactory actorFactory = new ActorFactory(entityRecordFactory, attackAction, befriendAction, moveAction, reproduceAction);
    private ReproduceAction reproduceAction = new ReproduceAction(actorFactory, logger, eventFactory, interakt, actionRecordFactory);

    @Test
    public void testGenerateTestDataCommand() {
        GenerateTestDataCommand generateTestDataCommand = new GenerateTestDataCommand(worldFactory, actorFactory);

        Console console = new Console();
        generateTestDataCommand.execute(console);

        Assert.assertTrue(PersistentData.getInstance().getNumWorlds() > 0);
        Assert.assertTrue(PersistentData.getInstance().getNumActors() > 0);
    }
}