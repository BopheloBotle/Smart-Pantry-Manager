package com.bophelobotle.blushpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bophelobotle.blushpantry.R;
import com.bophelobotle.blushpantry.models.PantryItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {

    public interface Listener {
        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<PantryItem> pantryItems) {
        items.clear();
        if (pantryItems != null) {
            items.addAll(pantryItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());
        if (item.getExpiryDate() == null || item.getExpiryDate().trim().isEmpty()) {
            holder.expiry.setText(R.string.no_expiry);
        } else {
            holder.expiry.setText("Expires " + item.getExpiryDate());
        }
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static String formatQuantity(double quantity) {
        if (Math.abs(quantity - Math.round(quantity)) < 0.0001) {
            return String.valueOf(Math.round(quantity));
        }
        return String.format(Locale.US, "%.2f", quantity);
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final MaterialButton edit;
        final MaterialButton delete;

        Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            quantity = itemView.findViewById(R.id.item_quantity);
            expiry = itemView.findViewById(R.id.item_expiry);
            edit = itemView.findViewById(R.id.btn_edit);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
