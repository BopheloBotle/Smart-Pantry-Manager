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

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
