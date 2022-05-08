package dansapps.interakt.actions;

import dansapps.interakt.Interakt;
import dansapps.interakt.actions.abs.Action;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.factories.ActionRecordFactory;
import dansapps.interakt.factories.ActorFactory;
import dansapps.interakt.factories.EventFactory;
import dansapps.interakt.objects.Actor;
import dansapps.interakt.objects.Event;
import dansapps.interakt.objects.World;
import dansapps.interakt.utils.Logger;

public class ReproduceAction implements Action {

    public static void execute(Actor actor, Actor other) {
        if (!actor.isFriend(other)) {
            return;
        }
        Actor offspring = ActorFactory.getInstance().createActorFromParents(actor, other);
        World world = actor.getWorld();
        boolean success = PersistentData.getInstance().placeIntoEnvironment(world, offspring); // TODO: place in same square as parents

        if (!success) {
            Logger.getInstance().logError("Something went wrong placing a new offspring into their environment.");
            return;
        }

        Event event = EventFactory.getInstance().createEvent(actor.getName() + " reproduced with " + other.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        Logger.getInstance().logEvent(event);

        if (actor.getName().equalsIgnoreCase(Interakt.getInstance().getPlayerActorName())) {
            Interakt.getInstance().getCommandSender().sendMessage("You reproduced with " + other.getName() + ", resulting in " + offspring.getName() + " coming into existence.");
        }

        if (other.getName().equalsIgnoreCase(Interakt.getInstance().getPlayerActorName())) {
            Interakt.getInstance().getCommandSender().sendMessage(other.getName() + " reproduced with you, resulting in " + offspring.getName() + " coming into existence.");
        }

        ActionRecordFactory.getInstance().createActionRecord(actor, new ReproduceAction());

        actor.increaseRelation(other, 100);
        other.increaseRelation(actor, 100);
    }

    @Override
    public String getName() {
        return "reproduce";
    }
}