package com.bophelobotle.blushpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bophelobotle.blushpantry.adapters.PantryAdapter;
import com.bophelobotle.blushpantry.database.DatabaseHelper;
import com.bophelobotle.blushpantry.models.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class PantryActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper database;
    private PantryAdapter adapter;
    private RecyclerView recycler;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.pantry_title);

        database = new DatabaseHelper(this);
        adapter = new PantryAdapter(this);
        recycler = findViewById(R.id.recycler_pantry);
        emptyView = findViewById(R.id.empty_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.btn_add_ingredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        NavigationHelper.bind(this, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<PantryItem> items = database.getAllPantryItems();
        adapter.submit(items);
        boolean empty = items.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(NavigationHelper.EXTRA_INGREDIENT_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, item.getName()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    database.deletePantryItem(item.getId());
                    refresh();
                })
                .show();
    }
}
