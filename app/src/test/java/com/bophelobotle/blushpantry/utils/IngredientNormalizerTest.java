package com.bophelobotle.blushpantry.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IngredientNormalizerTest {

    @Test
    public void singularAndPluralTomatoesMatch() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals(IngredientNormalizer.normalize("tomato"), IngredientNormalizer.normalize("tomatoes"));
    }

    @Test
    public void eggsMatchEgg() {
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
    }

    @Test
    public void riceIsNotBrokenByPluralRules() {
        assertEquals("rice", IngredientNormalizer.normalize("rice"));
    }

    @Test
    public void differentIngredientsStayDifferent() {
        assertNotEquals(IngredientNormalizer.normalize("onion"), IngredientNormalizer.normalize("spring onion"));
    }
}
