package com.bophelobotle.blushpantry;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Shared bottom navigation. Each main screen is its own Activity and we
 * pass IDs with explicit Intents, as the assignment requires.
 */
public final class NavigationHelper {

    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private NavigationHelper() {
    }

    public static void bind(Activity activity, int selectedItemId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottom_nav);
        if (nav == null) {
            return;
        }
        nav.setSelectedItemId(selectedItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) {
                return true;
            }
            Class<?> destination = null;
            if (id == R.id.nav_home) {
                destination = MainActivity.class;
            } else if (id == R.id.nav_pantry) {
                destination = PantryActivity.class;
            } else if (id == R.id.nav_recipes) {
                destination = SuggestedRecipesActivity.class;
            } else if (id == R.id.nav_settings) {
                destination = SettingsActivity.class;
            }
            if (destination != null) {
                Intent intent = new Intent(activity, destination);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            return true;
        });
    }
}
