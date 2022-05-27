package dansapps.interakt.actions;

import dansapps.interakt.Interakt;
import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.misc.enums.ACTION_TYPE;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.objects.World;
import dansapps.interakt.utils.Logger;

public class ReproduceAction implements Action {
    private ActorFactory actorFactory;
    private Logger logger;
    private EventFactory eventFactory;
    private Interakt interakt;
    private ActionRecordFactory actionRecordFactory;

    public ReproduceAction(ActorFactory actorFactory, Logger logger, EventFactory eventFactory, Interakt interakt, ActionRecordFactory actionRecordFactory) {
        this.actorFactory = actorFactory;
        this.logger = logger;
        this.eventFactory = eventFactory;
        this.interakt = interakt;
        this.actionRecordFactory = actionRecordFactory;
    }

    public void execute(Actor actor, Actor other) {
        if (!actor.isFriend(other)) {
            return;
        }
        Actor offspring = actorFactory.createActorFromParents(actor, other);
        World world = actor.getWorld();
        boolean success = PersistentData.getInstance().placeIntoEnvironment(world, offspring); // TODO: place in same square as parents

        if (!success) {
            logger.logError("Something went wrong placing a new offspring into their environment.");
            return;
        }

        Event event = eventFactory.createEvent(actor.getName() + " reproduced with " + other.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        logger.logEvent(event);

        if (actor.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage("You reproduced with " + other.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        }

        if (other.getName().equalsIgnoreCase(interakt.getPlayerActorName())) {
            interakt.getCommandSender().sendMessage(other.getName() + " reproduced with you, resulting in " + offspring.getName() + " coming into existence.");
        }

        actionRecordFactory.createActionRecord(actor, ACTION_TYPE.REPRODUCE);

        actor.increaseRelation(other, 100);
        other.increaseRelation(actor, 100);
    }

    @Override
    public String getName() {
        return "reproduce";
    }
}