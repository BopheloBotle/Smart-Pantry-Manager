package com.bophelobotle.blushpantry.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Turns ingredient names into a stable key for matching.
 *
 * This is a controlled strategy:
 * 1. Lowercase and trim.
 * 2. Strip punctuation and extra spaces.
 * 3. Apply a small alias map (chili/chilli, coriander/cilantro).
 * 4. Apply simple English plural rules (tomatoes -> tomato, eggs -> egg).
 *
 * It does NOT use substring matching, so "onion" will not match "spring onion"
 * and "tomato sauce" will not match "tomato".
 */
public final class IngredientNormalizer {

    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("chilli", "chili");
        ALIASES.put("chillies", "chili");
        ALIASES.put("chilies", "chili");
        ALIASES.put("coriander", "cilantro");
        ALIASES.put("garbanzo", "chickpea");
        ALIASES.put("garbanzo bean", "chickpea");
        ALIASES.put("chick pea", "chickpea");
        ALIASES.put("spring onion", "green onion");
        ALIASES.put("scallion", "green onion");
    }

    private IngredientNormalizer() {
    }

    public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }
        String value = rawName.toLowerCase(Locale.US).trim();
        value = value.replaceAll("[^a-z0-9\\s]", " ");
        value = value.replaceAll("\\s+", " ").trim();
        if (value.isEmpty()) {
            return "";
        }

        if (ALIASES.containsKey(value)) {
            value = ALIASES.get(value);
        }

        String[] parts = value.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(singularize(parts[i]));
        }
        String result = builder.toString();
        if (ALIASES.containsKey(result)) {
            result = ALIASES.get(result);
        }
        return result;
    }

    /**
     * Conservative singularisation. Words that would break if a trailing "s"
     * was removed (rice, oats, glass) are left unchanged.
     */
    static String singularize(String word) {
        if (word.length() <= 3) {
            return word;
        }
        if (word.equals("oats") || word.equals("rice") || word.equals("molasses")) {
            return word;
        }
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") && word.length() > 4) {
            // tomatoes -> tomato, potatoes -> potato
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses")
                || word.endsWith("xes") || word.endsWith("zes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
