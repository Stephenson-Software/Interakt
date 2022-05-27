package dansapps.interakt.objects;

import dansapps.interakt.misc.CONFIG;
import preponderous.environmentlib.abs.objects.Entity;

import java.util.Random;

public class FoodItem extends Entity {
    private double nutrition = new Random().nextDouble() * CONFIG.MAX_NUTRITION;

    public FoodItem(String name) {
        super(name);
    }

    public double getNutrition() {
        return nutrition;
    }

    public void setNutrition(double nutrition) {
        this.nutrition = nutrition;
    }
}
