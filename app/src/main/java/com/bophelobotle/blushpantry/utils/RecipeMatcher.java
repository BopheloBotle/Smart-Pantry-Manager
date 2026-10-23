package com.bophelobotle.blushpantry.utils;

import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decides which recipes the user can cook from the pantry they have right now.
 *
 * A recipe is suggested only when every required ingredient is present in at
 * least the required quantity. One missing ingredient, a short quantity, or
 * units that cannot be compared excludes the whole recipe.
 */
public class RecipeMatcher {

    public List<Recipe> findMakeableRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        List<Recipe> matches = new ArrayList<>();
        Map<String, List<PantryItem>> pantryIndex = indexPantry(pantryItems);

        if (recipes == null) {
            return matches;
        }
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantryIndex)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    public boolean canMake(Recipe recipe, List<PantryItem> pantryItems) {
        return canMake(recipe, indexPantry(pantryItems));
    }

    private boolean canMake(Recipe recipe, Map<String, List<PantryItem>> pantryIndex) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!hasEnough(required, pantryIndex)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasEnough(RecipeIngredient required, Map<String, List<PantryItem>> pantryIndex) {
        if (required == null) {
            return false;
        }
        String key = IngredientNormalizer.normalize(required.getName());
        if (key.isEmpty()) {
            return false;
        }
        List<PantryItem> candidates = pantryIndex.get(key);
        if (candidates == null || candidates.isEmpty() || required.getQuantity() <= 0) {
            return false;
        }

        double available = sumAvailableInRecipeUnit(candidates, required.getUnit());
        return !Double.isNaN(available) && available + 0.0001 >= required.getQuantity();
    }

    private double sumAvailableInRecipeUnit(List<PantryItem> candidates, String recipeUnit) {
        double available = 0;
        boolean convertedAny = false;
        for (PantryItem item : candidates) {
            double converted = UnitConverter.convert(
                    item.getQuantity(), item.getUnit(), recipeUnit);
            if (Double.isNaN(converted)) {
                continue;
            }
            convertedAny = true;
            available += converted;
        }
        return convertedAny ? available : Double.NaN;
    }

    private Map<String, List<PantryItem>> indexPantry(List<PantryItem> pantryItems) {
        Map<String, List<PantryItem>> index = new HashMap<>();
        if (pantryItems == null) {
            return index;
        }
        for (PantryItem item : pantryItems) {
            if (item == null) {
                continue;
            }
            String key = IngredientNormalizer.normalize(item.getName());
            if (key.isEmpty()) {
                continue;
            }
            List<PantryItem> list = index.get(key);
            if (list == null) {
                list = new ArrayList<>();
                index.put(key, list);
            }
            list.add(item);
        }
        return index;
    }
}
