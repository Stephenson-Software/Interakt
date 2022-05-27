package dansapps.interakt.factories;

import dansapps.interakt.objects.Event;
import dansapps.interakt.objects.FoodItem;
import dansapps.interakt.objects.Square;
import dansapps.interakt.utils.Logger;

public class FoodItemFactory {
    private final Logger logger;

    public FoodItemFactory(Logger logger) {
        this.logger = logger;
    }

    public void createFoodItemAt(String name, Square square) {
        FoodItem newFoodItem = new FoodItem(name);
        square.addEntity(newFoodItem);
        Event foodSpawnEvent = new Event("A new food item called " + newFoodItem.getName() + " has appeared at " + square.getX() + ", " + square.getY() + ".");
        logger.logEvent(foodSpawnEvent);
    }

}
