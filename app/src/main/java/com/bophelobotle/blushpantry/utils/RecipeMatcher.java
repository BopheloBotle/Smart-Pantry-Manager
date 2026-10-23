package com.bophelobotle.blushpantry.utils;

import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Matches pantry ingredients to saved recipes.
 *
 * findMakeableRecipes() is strict and returns only recipes whose full ingredient
 * quantities are available. findSuggestedRecipes() also returns useful partial
 * matches, ranking recipes the user can make first and then recipes needing the
 * fewest missing ingredients.
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

    /**
     * Returns recipes related to at least one ingredient in the pantry.
     * Complete matches appear first. Partial matches show what is missing.
     */
    public List<Recipe> findSuggestedRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        List<Recipe> suggestions = new ArrayList<>();
        Map<String, List<PantryItem>> pantryIndex = indexPantry(pantryItems);

        if (recipes == null || pantryIndex.isEmpty()) {
            return suggestions;
        }

        for (Recipe recipe : recipes) {
            if (recipe == null || recipe.getIngredients() == null
                    || recipe.getIngredients().isEmpty()) {
                continue;
            }

            int availableCount = 0;
            int presentCount = 0;
            List<String> missing = new ArrayList<>();

            for (RecipeIngredient required : recipe.getIngredients()) {
                if (required == null) {
                    continue;
                }

                String key = IngredientNormalizer.normalize(required.getName());
                List<PantryItem> candidates = pantryIndex.get(key);

                if (key.isEmpty() || candidates == null || candidates.isEmpty()) {
                    missing.add(formatRequired(required));
                    continue;
                }

                presentCount++;
                double available = sumAvailableInRecipeUnit(candidates, required.getUnit());
                if (!Double.isNaN(available) && required.getQuantity() > 0
                        && available + 0.0001 >= required.getQuantity()) {
                    availableCount++;
                } else {
                    missing.add(formatShortage(required, available));
                }
            }

            // Only suggest recipes that have a real ingredient-name overlap.
            // This also allows useful "almost there" suggestions when a pantry
            // ingredient exists but its quantity is too small.
            if (presentCount > 0) {
                recipe.setSuggestionAvailableIngredients(availableCount);
                recipe.setSuggestionTotalIngredients(recipe.getIngredients().size());
                recipe.setSuggestionMissingIngredients(missing);
                recipe.setSuggestionCanMake(missing.isEmpty());
                suggestions.add(recipe);
            }
        }

        // Makeable recipes first; then the recipes with the most covered
        // ingredients; then the smallest number of missing ingredients.
        Collections.sort(suggestions, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe first, Recipe second) {
                if (first.isSuggestionCanMake() != second.isSuggestionCanMake()) {
                    return first.isSuggestionCanMake() ? -1 : 1;
                }
                int availableCompare = Integer.compare(
                        second.getSuggestionAvailableIngredients(),
                        first.getSuggestionAvailableIngredients());
                if (availableCompare != 0) {
                    return availableCompare;
                }
                int missingCompare = Integer.compare(
                        first.getSuggestionMissingIngredients().size(),
                        second.getSuggestionMissingIngredients().size());
                if (missingCompare != 0) {
                    return missingCompare;
                }
                return safe(first.getName()).compareToIgnoreCase(safe(second.getName()));
            }
        });
        return suggestions;
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

    private String formatRequired(RecipeIngredient required) {
        return safe(required.getName()) + " (" + formatNumber(required.getQuantity())
                + unitSuffix(required.getUnit()) + ")";
    }

    private String formatShortage(RecipeIngredient required, double available) {
        String result = safe(required.getName()) + " (need "
                + formatNumber(required.getQuantity()) + unitSuffix(required.getUnit());
        if (!Double.isNaN(available) && available > 0) {
            result += ", have " + formatNumber(available) + unitSuffix(required.getUnit());
        }
        return result + ")";
    }

    private String unitSuffix(String unit) {
        return unit == null || unit.trim().isEmpty() ? "" : " " + unit.trim();
    }

    private String formatNumber(double value) {
        if (Math.abs(value - Math.round(value)) < 0.0001) {
            return String.valueOf(Math.round(value));
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
