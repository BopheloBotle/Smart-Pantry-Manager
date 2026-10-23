package com.bophelobotle.blushpantry;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bophelobotle.blushpantry.adapters.PantryAdapter;
import com.bophelobotle.blushpantry.database.DatabaseHelper;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.recipes_title);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(NavigationHelper.EXTRA_RECIPE_ID, -1);
        DatabaseHelper database = new DatabaseHelper(this);
        Recipe recipe = recipeId > 0 ? database.getRecipeWithIngredients(recipeId) : null;
        if (recipe == null) {
            Toast.makeText(this, R.string.missing_recipe, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView name = findViewById(R.id.detail_name);
        TextView description = findViewById(R.id.detail_description);
        TextView times = findViewById(R.id.detail_times);
        TextView instructions = findViewById(R.id.detail_instructions);
        LinearLayout ingredientList = findViewById(R.id.detail_ingredients);

        name.setText(recipe.getName());
        description.setText(recipe.getDescription());
        times.setText(getString(R.string.prep_time, recipe.getPrepMinutes())
                + "  ·  " + getString(R.string.cook_time, recipe.getCookMinutes()));
        instructions.setText(recipe.getInstructions());

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            TextView row = new TextView(this);
            row.setText("• " + PantryAdapter.formatQuantity(ingredient.getQuantity())
                    + " " + ingredient.getUnit() + " " + ingredient.getName());
            row.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            row.setPadding(0, 6, 0, 6);
            ingredientList.addView(row);
        }
    }
}
