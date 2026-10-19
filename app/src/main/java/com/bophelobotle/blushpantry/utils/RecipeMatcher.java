package com.bophelobotle.blushpantry.utils;

import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strict pantry-to-recipe matching.
 *
 * A recipe is suggested ONLY when every required ingredient is present in the
 * pantry in a sufficient quantity. Partial matches are never suggested.
 *
 * Algorithm:
 * 1. Build a pantry index keyed by normalized ingredient name. Each key maps
 *    to the list of pantry rows with that name (a user might store "tomatoes"
 *    twice in different units).
 * 2. For each recipe, inspect every required ingredient line.
 * 3. Look up the normalized name in the pantry index. Missing name = fail.
 * 4. Convert each matching pantry row into the recipe's unit using
 *    {@link UnitConverter}. Rows with incompatible units are ignored.
 * 5. Sum the converted pantry quantity. If the sum is NaN (nothing converted)
 *    or the sum is less than the required amount, the recipe fails.
 * 6. The recipe is included only if every ingredient line passed.
 *
 * This class does not implement an "Almost There" list. Any future close-match
 * feature must live in a different method so it cannot leak into suggestions.
 */
public class RecipeMatcher {

    public List<Recipe> findMakeableRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        List<Recipe> matches = new ArrayList<>();
        Map<String, List<PantryItem>> pantryIndex = indexPantry(pantryItems);

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

    /**
     * Returns true only when the pantry can supply this one ingredient line.
     */
    private boolean hasEnough(RecipeIngredient required, Map<String, List<PantryItem>> pantryIndex) {
        if (required == null) {
            return false;
        }
        String key = IngredientNormalizer.normalize(required.getName());
        if (key.isEmpty()) {
            return false;
        }
        List<PantryItem> candidates = pantryIndex.get(key);
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }

        double requiredAmount = required.getQuantity();
        if (requiredAmount <= 0) {
            // Quantity of zero would be unsafe to treat as "any amount".
            return false;
        }

        double available = 0;
        boolean convertedAny = false;
        for (PantryItem item : candidates) {
            double converted = UnitConverter.convert(item.getQuantity(), item.getUnit(), required.getUnit());
            if (Double.isNaN(converted)) {
                continue;
            }
            convertedAny = true;
            available += converted;
        }

        if (!convertedAny) {
            // Name matched, but units could not be compared safely.
            return false;
        }
        // Tiny floating-point buffer so 1000g vs 1kg still matches.
        return available + 0.0001 >= requiredAmount;
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
