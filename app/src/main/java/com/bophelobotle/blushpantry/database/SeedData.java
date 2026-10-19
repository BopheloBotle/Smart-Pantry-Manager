package com.bophelobotle.blushpantry.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Inserts 22 everyday recipes the first time the database is created.
 * This class is never called on later app launches, so pantry data stays intact.
 */
final class SeedData {

    private SeedData() {
    }

    static void insertRecipes(SQLiteDatabase db) {
        long scrambled = recipe(db, "Scrambled Eggs",
                "Soft breakfast eggs cooked in butter.", 5, 5,
                "1. Beat the eggs with milk and a pinch of salt.\n2. Melt butter in a pan on low heat.\n3. Pour in the eggs and stir gently until just set.");
        ingredient(db, scrambled, "egg", 3, "piece");
        ingredient(db, scrambled, "milk", 30, "ml");
        ingredient(db, scrambled, "butter", 10, "g");
        ingredient(db, scrambled, "salt", 1, "tsp");

        long cheeseToast = recipe(db, "Cheese Toast",
                "Crisp toast with melted cheese.", 3, 5,
                "1. Butter the bread.\n2. Add cheese on top.\n3. Toast in a pan or grill until the cheese melts.");
        ingredient(db, cheeseToast, "bread", 2, "slice");
        ingredient(db, cheeseToast, "cheese", 40, "g");
        ingredient(db, cheeseToast, "butter", 10, "g");

        long salad = recipe(db, "Tomato Cucumber Salad",
                "A fresh no-cook side salad.", 8, 0,
                "1. Slice tomatoes and cucumber.\n2. Toss with oil and salt.\n3. Serve immediately.");
        ingredient(db, salad, "tomato", 2, "piece");
        ingredient(db, salad, "cucumber", 1, "piece");
        ingredient(db, salad, "oil", 15, "ml");
        ingredient(db, salad, "salt", 1, "tsp");

        long garlicPasta = recipe(db, "Garlic Butter Pasta",
                "Simple pasta with garlic butter.", 5, 12,
                "1. Boil pasta in salted water until tender.\n2. Melt butter and cook garlic for 1 minute.\n3. Toss drained pasta in the garlic butter.");
        ingredient(db, garlicPasta, "pasta", 120, "g");
        ingredient(db, garlicPasta, "butter", 20, "g");
        ingredient(db, garlicPasta, "garlic", 2, "clove");
        ingredient(db, garlicPasta, "salt", 1, "tsp");

        long oatBowl = recipe(db, "Banana Oat Bowl",
                "Warm oats with sliced banana.", 3, 8,
                "1. Simmer oats in milk until creamy.\n2. Slice banana on top.\n3. Serve warm.");
        ingredient(db, oatBowl, "oats", 50, "g");
        ingredient(db, oatBowl, "banana", 1, "piece");
        ingredient(db, oatBowl, "milk", 200, "ml");

        long pbToast = recipe(db, "Peanut Butter Banana Toast",
                "Quick toast with peanut butter and banana.", 4, 3,
                "1. Toast the bread.\n2. Spread peanut butter.\n3. Add banana slices.");
        ingredient(db, pbToast, "bread", 2, "slice");
        ingredient(db, pbToast, "peanut butter", 30, "g");
        ingredient(db, pbToast, "banana", 1, "piece");

        long friedRice = recipe(db, "Egg Fried Rice",
                "Everyday fried rice with egg and onion.", 8, 10,
                "1. Beat the egg and scramble it in oil.\n2. Add onion and cooked rice.\n3. Season with salt and stir-fry until hot.");
        ingredient(db, friedRice, "rice", 150, "g");
        ingredient(db, friedRice, "egg", 1, "piece");
        ingredient(db, friedRice, "onion", 1, "piece");
        ingredient(db, friedRice, "oil", 15, "ml");
        ingredient(db, friedRice, "salt", 1, "tsp");

        long hash = recipe(db, "Potato Hash",
                "Crispy pan potatoes with onion.", 10, 15,
                "1. Dice potato and onion.\n2. Fry in oil until golden.\n3. Season with salt.");
        ingredient(db, hash, "potato", 2, "piece");
        ingredient(db, hash, "onion", 1, "piece");
        ingredient(db, hash, "oil", 15, "ml");
        ingredient(db, hash, "salt", 1, "tsp");

        long omelette = recipe(db, "Tomato Onion Omelette",
                "Fluffy omelette with tomato and onion.", 6, 6,
                "1. Beat eggs with salt.\n2. Cook onion and tomato in oil.\n3. Pour eggs over the vegetables and cook until set.");
        ingredient(db, omelette, "egg", 3, "piece");
        ingredient(db, omelette, "tomato", 1, "piece");
        ingredient(db, omelette, "onion", 0.5, "piece");
        ingredient(db, omelette, "oil", 10, "ml");
        ingredient(db, omelette, "salt", 1, "tsp");

        long pancakes = recipe(db, "Everyday Pancakes",
                "Soft pancakes from pantry staples.", 8, 12,
                "1. Mix flour, milk, egg, and sugar into a batter.\n2. Cook spoonfuls in a lightly oiled pan.\n3. Flip when bubbles form and cook the other side.");
        ingredient(db, pancakes, "flour", 120, "g");
        ingredient(db, pancakes, "milk", 180, "ml");
        ingredient(db, pancakes, "egg", 1, "piece");
        ingredient(db, pancakes, "sugar", 15, "g");
        ingredient(db, pancakes, "oil", 10, "ml");

        long frenchToast = recipe(db, "French Toast",
                "Bread soaked in egg and milk, then fried.", 6, 8,
                "1. Whisk egg, milk, and sugar.\n2. Dip bread slices in the mixture.\n3. Fry in butter until golden on both sides.");
        ingredient(db, frenchToast, "bread", 3, "slice");
        ingredient(db, frenchToast, "egg", 2, "piece");
        ingredient(db, frenchToast, "milk", 60, "ml");
        ingredient(db, frenchToast, "sugar", 10, "g");
        ingredient(db, frenchToast, "butter", 15, "g");

        long tuna = recipe(db, "Tuna Sandwich",
                "No-cook sandwich with tuna and onion.", 7, 0,
                "1. Mix tuna with finely chopped onion.\n2. Spoon onto bread.\n3. Close the sandwich and serve.");
        ingredient(db, tuna, "bread", 2, "slice");
        ingredient(db, tuna, "tuna", 80, "g");
        ingredient(db, tuna, "onion", 0.25, "piece");

        long tomatoPasta = recipe(db, "Tomato Garlic Pasta",
                "Pasta in a quick tomato garlic sauce.", 5, 15,
                "1. Boil pasta until tender.\n2. Cook garlic in oil, then add chopped tomato and salt.\n3. Simmer and toss with pasta.");
        ingredient(db, tomatoPasta, "pasta", 120, "g");
        ingredient(db, tomatoPasta, "tomato", 3, "piece");
        ingredient(db, tomatoPasta, "garlic", 2, "clove");
        ingredient(db, tomatoPasta, "oil", 15, "ml");
        ingredient(db, tomatoPasta, "salt", 1, "tsp");

        long riceBeans = recipe(db, "Rice and Beans",
                "Filling rice with onion and beans.", 8, 20,
                "1. Cook rice until fluffy.\n2. Fry onion in oil, add beans and salt.\n3. Serve beans over rice.");
        ingredient(db, riceBeans, "rice", 150, "g");
        ingredient(db, riceBeans, "beans", 200, "g");
        ingredient(db, riceBeans, "onion", 1, "piece");
        ingredient(db, riceBeans, "oil", 15, "ml");
        ingredient(db, riceBeans, "salt", 1, "tsp");

        long mash = recipe(db, "Mashed Potatoes",
                "Creamy mashed potatoes.", 8, 18,
                "1. Boil potatoes until soft.\n2. Mash with milk, butter, and salt.\n3. Serve warm.");
        ingredient(db, mash, "potato", 3, "piece");
        ingredient(db, mash, "milk", 60, "ml");
        ingredient(db, mash, "butter", 20, "g");
        ingredient(db, mash, "salt", 1, "tsp");

        long carrotRice = recipe(db, "Carrot Rice Bowl",
                "Rice with sweet fried carrot.", 8, 15,
                "1. Cook rice.\n2. Fry grated or diced carrot in oil with salt.\n3. Serve carrot over rice.");
        ingredient(db, carrotRice, "rice", 150, "g");
        ingredient(db, carrotRice, "carrot", 2, "piece");
        ingredient(db, carrotRice, "oil", 10, "ml");
        ingredient(db, carrotRice, "salt", 1, "tsp");

        long garlicBread = recipe(db, "Garlic Bread",
                "Toasted bread with garlic butter.", 5, 6,
                "1. Mix butter with crushed garlic.\n2. Spread on bread slices.\n3. Toast until golden.");
        ingredient(db, garlicBread, "bread", 3, "slice");
        ingredient(db, garlicBread, "butter", 20, "g");
        ingredient(db, garlicBread, "garlic", 2, "clove");

        long spinachEggs = recipe(db, "Spinach Eggs",
                "Eggs scrambled with spinach.", 5, 7,
                "1. Wilt spinach in oil.\n2. Pour in beaten eggs and salt.\n3. Stir until the eggs are just cooked.");
        ingredient(db, spinachEggs, "egg", 2, "piece");
        ingredient(db, spinachEggs, "spinach", 80, "g");
        ingredient(db, spinachEggs, "oil", 10, "ml");
        ingredient(db, spinachEggs, "salt", 1, "tsp");

        long lemonPotato = recipe(db, "Lemon Potato Salad",
                "Warm potato salad with lemon and onion.", 10, 15,
                "1. Boil potatoes until tender.\n2. Toss with onion, oil, lemon, and salt.\n3. Serve warm or cold.");
        ingredient(db, lemonPotato, "potato", 3, "piece");
        ingredient(db, lemonPotato, "onion", 0.5, "piece");
        ingredient(db, lemonPotato, "oil", 15, "ml");
        ingredient(db, lemonPotato, "lemon", 1, "piece");
        ingredient(db, lemonPotato, "salt", 1, "tsp");

        long chickenRice = recipe(db, "Chicken Rice Bowl",
                "Simple chicken served over rice.", 10, 20,
                "1. Cook rice.\n2. Fry onion, then add chicken pieces, oil, and salt.\n3. Cook through and serve over rice.");
        ingredient(db, chickenRice, "chicken", 200, "g");
        ingredient(db, chickenRice, "rice", 150, "g");
        ingredient(db, chickenRice, "onion", 1, "piece");
        ingredient(db, chickenRice, "oil", 15, "ml");
        ingredient(db, chickenRice, "salt", 1, "tsp");

        long butterNoodles = recipe(db, "Butter Noodles",
                "The simplest pasta bowl.", 2, 10,
                "1. Boil pasta in salted water.\n2. Drain and toss with butter.\n3. Taste and add a little more salt if needed.");
        ingredient(db, butterNoodles, "pasta", 120, "g");
        ingredient(db, butterNoodles, "butter", 20, "g");
        ingredient(db, butterNoodles, "salt", 1, "tsp");

        long tomatoCheese = recipe(db, "Tomato Cheese Toast",
                "Open toast with tomato and cheese.", 5, 6,
                "1. Layer tomato on bread and drizzle oil.\n2. Add cheese.\n3. Grill or pan-toast until the cheese melts.");
        ingredient(db, tomatoCheese, "bread", 2, "slice");
        ingredient(db, tomatoCheese, "tomato", 1, "piece");
        ingredient(db, tomatoCheese, "cheese", 40, "g");
        ingredient(db, tomatoCheese, "oil", 5, "ml");
    }

    private static long recipe(SQLiteDatabase db, String name, String description,
                              int prep, int cook, String instructions) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RECIPE_NAME, name);
        values.put(DatabaseHelper.COL_RECIPE_DESCRIPTION, description);
        values.put(DatabaseHelper.COL_PREP_MINUTES, prep);
        values.put(DatabaseHelper.COL_COOK_MINUTES, cook);
        values.put(DatabaseHelper.COL_INSTRUCTIONS, instructions);
        return db.insert(DatabaseHelper.TABLE_RECIPES, null, values);
    }

    private static void ingredient(SQLiteDatabase db, long recipeId, String name,
                                   double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ING_RECIPE_ID, recipeId);
        values.put(DatabaseHelper.COL_ING_NAME, name);
        values.put(DatabaseHelper.COL_ING_QUANTITY, quantity);
        values.put(DatabaseHelper.COL_ING_UNIT, unit);
        db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
    }
}
