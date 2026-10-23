package com.bophelobotle.blushpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bophelobotle.blushpantry.R;
import com.bophelobotle.blushpantry.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {

    public interface Listener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Recipe> values) {
        recipes.clear();
        if (values != null) {
            recipes.addAll(values);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());
        holder.description.setText(recipe.getDescription());
        holder.time.setText(holder.itemView.getContext()
                .getString(R.string.recipe_total_time,
                        recipe.getPrepMinutes() + recipe.getCookMinutes()));

        if (recipe.isSuggestionCanMake()) {
            holder.match.setText(R.string.recipe_ready_to_make);
        } else {
            StringBuilder details = new StringBuilder();
            details.append(holder.itemView.getContext().getString(
                    R.string.recipe_ingredient_progress,
                    recipe.getSuggestionAvailableIngredients(),
                    recipe.getSuggestionTotalIngredients()));
            List<String> missing = recipe.getSuggestionMissingIngredients();
            if (missing != null && !missing.isEmpty()) {
                details.append("\n")
                        .append(holder.itemView.getContext().getString(R.string.recipe_missing_label))
                        .append(" ");
                for (int i = 0; i < missing.size(); i++) {
                    if (i > 0) {
                        details.append(", ");
                    }
                    details.append(missing.get(i));
                }
            }
            holder.match.setText(details.toString());
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView time;
        final TextView match;

        Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name);
            description = itemView.findViewById(R.id.recipe_description);
            time = itemView.findViewById(R.id.recipe_time);
            match = itemView.findViewById(R.id.recipe_match);
        }
    }
}
