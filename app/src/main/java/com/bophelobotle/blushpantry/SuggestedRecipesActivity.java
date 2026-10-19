package com.bophelobotle.blushpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bophelobotle.blushpantry.adapters.RecipeAdapter;
import com.bophelobotle.blushpantry.database.DatabaseHelper;
import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.utils.RecipeMatcher;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper database;
    private RecipeAdapter adapter;
    private RecyclerView recycler;
    private View emptyView;
    private final RecipeMatcher matcher = new RecipeMatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.recipes_title);

        database = new DatabaseHelper(this);
        adapter = new RecipeAdapter(this);
        recycler = findViewById(R.id.recycler_recipes);
        emptyView = findViewById(R.id.empty_recipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        NavigationHelper.bind(this, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> pantry = database.getAllPantryItems();
        List<Recipe> recipes = database.getAllRecipesWithIngredients();
        List<Recipe> matches = matcher.findMakeableRecipes(recipes, pantry);
        adapter.submit(matches);
        boolean empty = matches.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(NavigationHelper.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
