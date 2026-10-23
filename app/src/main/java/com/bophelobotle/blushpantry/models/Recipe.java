package com.bophelobotle.blushpantry.models;

import java.util.ArrayList;
import java.util.List;

/** A complete recipe with the ingredients required to cook it. */
public class Recipe {
    private long id;
    private String name;
    private String description;
    private int prepMinutes;
    private int cookMinutes;
    private String instructions;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String name, String description, int prepMinutes, int cookMinutes, String instructions) {
        this.name = name;
        this.description = description;
        this.prepMinutes = prepMinutes;
        this.cookMinutes = cookMinutes;
        this.instructions = instructions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrepMinutes() {
        return prepMinutes;
    }

    public void setPrepMinutes(int prepMinutes) {
        this.prepMinutes = prepMinutes;
    }

    public int getCookMinutes() {
        return cookMinutes;
    }

    public void setCookMinutes(int cookMinutes) {
        this.cookMinutes = cookMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    // Runtime-only metadata used by the suggested-recipes screen.
    private int suggestionAvailableIngredients;
    private int suggestionTotalIngredients;
    private List<String> suggestionMissingIngredients = new ArrayList<>();
    private boolean suggestionCanMake;

    public int getSuggestionAvailableIngredients() {
        return suggestionAvailableIngredients;
    }

    public void setSuggestionAvailableIngredients(int value) {
        this.suggestionAvailableIngredients = value;
    }

    public int getSuggestionTotalIngredients() {
        return suggestionTotalIngredients;
    }

    public void setSuggestionTotalIngredients(int value) {
        this.suggestionTotalIngredients = value;
    }

    public List<String> getSuggestionMissingIngredients() {
        return suggestionMissingIngredients;
    }

    public void setSuggestionMissingIngredients(List<String> value) {
        this.suggestionMissingIngredients = value == null ? new ArrayList<>() : value;
    }

    public boolean isSuggestionCanMake() {
        return suggestionCanMake;
    }

    public void setSuggestionCanMake(boolean value) {
        this.suggestionCanMake = value;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
