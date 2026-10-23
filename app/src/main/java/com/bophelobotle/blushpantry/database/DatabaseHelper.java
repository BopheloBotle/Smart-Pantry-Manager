package com.bophelobotle.blushpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.models.Recipe;
import com.bophelobotle.blushpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteOpenHelper for pantry items, recipes, and recipe ingredients.
 * Recipes are seeded only inside {@link #onCreate}, which Android calls once
 * when the database file does not exist yet.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "blush_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_DESCRIPTION = "description";
    public static final String COL_PREP_MINUTES = "prep_minutes";
    public static final String COL_COOK_MINUTES = "cook_minutes";
    public static final String COL_INSTRUCTIONS = "instructions";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_ING_ID = "id";
    public static final String COL_ING_RECIPE_ID = "recipe_id";
    public static final String COL_ING_NAME = "name";
    public static final String COL_ING_QUANTITY = "quantity";
    public static final String COL_ING_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PANTRY_NAME + " TEXT NOT NULL, "
                + COL_PANTRY_QUANTITY + " REAL NOT NULL, "
                + COL_PANTRY_UNIT + " TEXT NOT NULL, "
                + COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT NOT NULL, "
                + COL_RECIPE_DESCRIPTION + " TEXT NOT NULL, "
                + COL_PREP_MINUTES + " INTEGER NOT NULL, "
                + COL_COOK_MINUTES + " INTEGER NOT NULL, "
                + COL_INSTRUCTIONS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ING_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_ING_NAME + " TEXT NOT NULL, "
                + COL_ING_QUANTITY + " REAL NOT NULL, "
                + COL_ING_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_ING_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        SeedData.insertRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 has no upgrade path. Future versions must migrate without
        // dropping pantry_items so user data is not deleted.
    }

    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PANTRY, null, toPantryValues(item));
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, toPantryValues(item),
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = fromPantryCursor(cursor);
        }
        cursor.close();
        return item;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_PANTRY_NAME + " COLLATE NOCASE ASC");
        while (cursor.moveToNext()) {
            items.add(fromPantryCursor(cursor));
        }
        cursor.close();
        return items;
    }

    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " COLLATE NOCASE ASC");
        while (cursor.moveToNext()) {
            Recipe recipe = fromRecipeCursor(cursor);
            recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
            recipes.add(recipe);
        }
        cursor.close();
        return recipes;
    }

    public Recipe getRecipeWithIngredients(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = fromRecipeCursor(cursor);
            recipe.setIngredients(getIngredientsForRecipe(id));
        }
        cursor.close();
        return recipe;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_ING_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, COL_ING_ID + " ASC");
        while (cursor.moveToNext()) {
            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ING_ID)));
            ingredient.setRecipeId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ING_RECIPE_ID)));
            ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_NAME)));
            ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ING_QUANTITY)));
            ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_UNIT)));
            ingredients.add(ingredient);
        }
        cursor.close();
        return ingredients;
    }

    private ContentValues toPantryValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        return values;
    }

    private PantryItem fromPantryCursor(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY);
        item.setExpiryDate(cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
        return item;
    }

    private Recipe fromRecipeCursor(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
        recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
        recipe.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_DESCRIPTION)));
        recipe.setPrepMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREP_MINUTES)));
        recipe.setCookMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(COL_COOK_MINUTES)));
        recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
        return recipe;
    }
}
