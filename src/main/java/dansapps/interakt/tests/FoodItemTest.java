package dansapps.interakt.tests;

import dansapps.interakt.misc.CONFIG;
import dansapps.interakt.objects.FoodItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Characterization tests for FoodItem: it keeps the name it was given, starts with a random
 * nutrition value in [0, CONFIG.MAX_NUTRITION), and that value can be overwritten.
 */
public class FoodItemTest {

    @Test
    public void testNameIsKept() {
        Assert.assertEquals("Gerald", new FoodItem("Gerald").getName());
    }

    @Test
    public void testFoodItemsWithTheSameNameHaveDifferentUUIDs() {
        FoodItem first = new FoodItem("Gerald");
        FoodItem second = new FoodItem("Gerald");

        Assert.assertNotNull(first.getUUID());
        Assert.assertFalse(first.getUUID().equals(second.getUUID()));
    }

    @Test
    public void testStartingNutritionIsWithinTheConfiguredMaximum() {
        for (int i = 0; i < 100; i++) {
            double nutrition = new FoodItem("Gerald").getNutrition();
            Assert.assertTrue(String.valueOf(nutrition), nutrition >= 0);
            Assert.assertTrue(String.valueOf(nutrition), nutrition < CONFIG.MAX_NUTRITION);
        }
    }

    @Test
    public void testSetNutritionOverwritesTheValue() {
        FoodItem foodItem = new FoodItem("Gerald");

        foodItem.setNutrition(123.5);

        Assert.assertEquals(123.5, foodItem.getNutrition(), 0.0);
    }
}
