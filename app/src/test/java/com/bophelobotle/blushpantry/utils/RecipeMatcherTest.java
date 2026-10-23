package com.bophelobotle.blushpantry.utils;

import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeMatcherTest {

    private RecipeMatcher matcher;
    private Recipe tomatoEgg;

    @Before
    public void setUp() {
        matcher = new RecipeMatcher();
        tomatoEgg = recipe("Tomato Eggs",
                ingredient("eggs", 3, "piece"),
                ingredient("tomatoes", 2, "piece"));
    }

    @Test
    public void matchesWhenEveryIngredientAndQuantityIsPresent() {
        List<PantryItem> pantry = Arrays.asList(
                pantry("eggs", 3, "piece"),
                pantry("tomatoes", 2, "piece"));
        assertTrue(matcher.canMake(tomatoEgg, pantry));
        assertEquals(1, matcher.findMakeableRecipes(Collections.singletonList(tomatoEgg), pantry).size());
    }

    @Test
    public void excludesWhenOneIngredientIsMissing() {
        List<PantryItem> pantry = Collections.singletonList(pantry("eggs", 3, "piece"));
        assertFalse(matcher.canMake(tomatoEgg, pantry));
    }

    @Test
    public void excludesWhenQuantityIsInsufficient() {
        List<PantryItem> pantry = Arrays.asList(
                pantry("eggs", 3, "piece"),
                pantry("tomatoes", 1, "piece"));
        assertFalse(matcher.canMake(tomatoEgg, pantry));
    }

    @Test
    public void matchesSimpleSingularPluralNames() {
        List<PantryItem> pantry = Arrays.asList(
                pantry("egg", 3, "piece"),
                pantry("tomato", 2, "piece"));
        assertTrue(matcher.canMake(tomatoEgg, pantry));
    }

    @Test
    public void suggestionsIncludePartialMatchesAndExplainMissingIngredients() {
        Recipe recipe = recipe("Egg and Tomato Bowl",
                ingredient("egg", 2, "piece"),
                ingredient("tomato", 2, "piece"),
                ingredient("cheese", 30, "g"));
        List<PantryItem> pantry = Arrays.asList(
                pantry("eggs", 2, "piece"),
                pantry("tomatoes", 1, "piece"));

        List<Recipe> suggestions = matcher.findSuggestedRecipes(
                Collections.singletonList(recipe), pantry);

        assertEquals(1, suggestions.size());
        assertFalse(suggestions.get(0).isSuggestionCanMake());
        assertEquals(1, suggestions.get(0).getSuggestionAvailableIngredients());
        assertEquals(3, suggestions.get(0).getSuggestionTotalIngredients());
        assertEquals(2, suggestions.get(0).getSuggestionMissingIngredients().size());
    }

    @Test
    public void makeableSuggestionsAreRankedBeforePartialMatches() {
        Recipe complete = recipe("Eggs", ingredient("egg", 1, "piece"));
        Recipe partial = recipe("Egg and Cheese", ingredient("egg", 1, "piece"),
                ingredient("cheese", 20, "g"));
        List<Recipe> suggestions = matcher.findSuggestedRecipes(
                Arrays.asList(partial, complete),
                Collections.singletonList(pantry("egg", 2, "piece")));

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.get(0).isSuggestionCanMake());
        assertEquals("Eggs", suggestions.get(0).getName());
        assertFalse(suggestions.get(1).isSuggestionCanMake());
    }

    @Test
    public void suggestionsDoNotIncludeRecipesWithNoPantryIngredientOverlap() {
        Recipe recipe = recipe("Pasta", ingredient("pasta", 100, "g"));
        List<Recipe> suggestions = matcher.findSuggestedRecipes(
                Collections.singletonList(recipe),
                Collections.singletonList(pantry("rice", 100, "g")));
        assertTrue(suggestions.isEmpty());
    }

    @Test
    public void emptyPantryMatchesNothing() {
        assertFalse(matcher.canMake(tomatoEgg, new ArrayList<>()));
        assertTrue(matcher.findMakeableRecipes(Collections.singletonList(tomatoEgg), new ArrayList<>()).isEmpty());
    }

    @Test
    public void convertsCompatibleMassUnits() {
        Recipe recipe = recipe("Butter toast", ingredient("butter", 1, "kg"));
        List<PantryItem> pantry = Collections.singletonList(pantry("butter", 1000, "g"));
        assertTrue(matcher.canMake(recipe, pantry));
    }

    @Test
    public void convertsCompatibleVolumeUnits() {
        Recipe recipe = recipe("Milk drink", ingredient("milk", 1, "l"));
        List<PantryItem> pantry = Collections.singletonList(pantry("milk", 1000, "ml"));
        assertTrue(matcher.canMake(recipe, pantry));
    }

    @Test
    public void rejectsIncompatibleUnits() {
        Recipe recipe = recipe("Tomato salad", ingredient("tomato", 2, "piece"));
        List<PantryItem> pantry = Collections.singletonList(pantry("tomato", 200, "g"));
        assertFalse(matcher.canMake(recipe, pantry));
    }

    @Test
    public void doesNotUseSubstringMatching() {
        Recipe recipe = recipe("Onion soup", ingredient("onion", 1, "piece"));
        List<PantryItem> pantry = Collections.singletonList(pantry("spring onion", 2, "piece"));
        assertFalse(matcher.canMake(recipe, pantry));
    }

    @Test
    public void sumsMultiplePantryRowsOfTheSameIngredient() {
        Recipe recipe = recipe("Eggs", ingredient("egg", 3, "piece"));
        List<PantryItem> pantry = Arrays.asList(
                pantry("eggs", 1, "piece"),
                pantry("egg", 2, "piece"));
        assertTrue(matcher.canMake(recipe, pantry));
    }

    @Test
    public void extraPantryItemsDoNotCauseAMatchOnTheirOwn() {
        Recipe recipe = recipe("Needs lemon", ingredient("lemon", 1, "piece"));
        List<PantryItem> pantry = Arrays.asList(
                pantry("egg", 12, "piece"),
                pantry("tomato", 8, "piece"),
                pantry("bread", 10, "slice"));
        assertFalse(matcher.canMake(recipe, pantry));
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(name, "test", 5, 5, "Mix.");
        recipe.setIngredients(Arrays.asList(ingredients));
        return recipe;
    }

    private static RecipeIngredient ingredient(String name, double quantity, String unit) {
        return new RecipeIngredient(1, name, quantity, unit);
    }

    private static PantryItem pantry(String name, double quantity, String unit) {
        return new PantryItem(1, name, quantity, unit, null);
    }
}
