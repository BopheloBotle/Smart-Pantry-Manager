package com.bophelobotle.blushpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bophelobotle.blushpantry.database.DatabaseHelper;
import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.utils.DateValidator;
import com.bophelobotle.blushpantry.utils.PreferencesHelper;
import com.bophelobotle.blushpantry.utils.RecipeMatcher;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private PreferencesHelper preferences;
    private final RecipeMatcher matcher = new RecipeMatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        database = new DatabaseHelper(this);
        preferences = new PreferencesHelper(this);
        NavigationHelper.bind(this, R.id.nav_home);

        findViewById(R.id.btn_open_pantry).setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)));
        findViewById(R.id.btn_open_recipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.btn_open_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {
        List<PantryItem> pantry = database.getAllPantryItems();
        List<Recipe> recipes = database.getAllRecipesWithIngredients();
        List<Recipe> makeable = matcher.findMakeableRecipes(recipes, pantry);

        TextView pantryCount = findViewById(R.id.stat_pantry_count);
        TextView recipeCount = findViewById(R.id.stat_recipe_count);
        pantryCount.setText(String.valueOf(pantry.size()));
        recipeCount.setText(String.valueOf(makeable.size()));

        TextView expiringMessage = findViewById(R.id.expiring_message);
        LinearLayout expiringList = findViewById(R.id.expiring_list);
        expiringList.removeAllViews();

        if (!preferences.isExpiryRemindersEnabled()) {
            expiringMessage.setVisibility(View.VISIBLE);
            expiringMessage.setText(R.string.reminders_off);
            return;
        }

        List<PantryItem> soon = new ArrayList<>();
        for (PantryItem item : pantry) {
            if (DateValidator.isExpiringSoon(item.getExpiryDate(), 3)) {
                soon.add(item);
            }
        }

        if (soon.isEmpty()) {
            expiringMessage.setVisibility(View.VISIBLE);
            expiringMessage.setText(R.string.no_expiring);
        } else {
            expiringMessage.setVisibility(View.GONE);
            for (PantryItem item : soon) {
                TextView row = new TextView(this);
                row.setText(item.getName() + " · " + item.getExpiryDate());
                row.setTextColor(ContextCompat.getColor(this, R.color.blush_dark));
                row.setPadding(0, 8, 0, 8);
                expiringList.addView(row);
            }
        }
    }
}
