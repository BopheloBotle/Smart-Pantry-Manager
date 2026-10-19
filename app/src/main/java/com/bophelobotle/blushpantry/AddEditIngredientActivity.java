package com.bophelobotle.blushpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bophelobotle.blushpantry.database.DatabaseHelper;
import com.bophelobotle.blushpantry.models.PantryItem;
import com.bophelobotle.blushpantry.utils.DateValidator;
import com.bophelobotle.blushpantry.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private long editingId = -1;
    private String selectedExpiry = null;

    private TextInputLayout nameLayout;
    private TextInputLayout quantityLayout;
    private TextInputEditText nameInput;
    private TextInputEditText quantityInput;
    private Spinner unitSpinner;
    private TextView expiryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        database = new DatabaseHelper(this);
        editingId = getIntent().getLongExtra(NavigationHelper.EXTRA_INGREDIENT_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(editingId > 0 ? R.string.form_edit_title : R.string.form_add_title);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        unitSpinner = findViewById(R.id.spinner_unit);
        expiryText = findViewById(R.id.text_expiry);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
        applyPreferredUnitDefault();

        expiryText.setText(R.string.no_expiry);
        findViewById(R.id.btn_pick_date).setOnClickListener(v -> pickDate());
        findViewById(R.id.btn_clear_date).setOnClickListener(v -> {
            selectedExpiry = null;
            expiryText.setText(R.string.no_expiry);
        });
        findViewById(R.id.btn_save).setOnClickListener(v -> save());

        if (editingId > 0) {
            loadItem();
        }
    }

    private void applyPreferredUnitDefault() {
        PreferencesHelper prefs = new PreferencesHelper(this);
        String preferred = prefs.getPreferredUnits();
        String[] units = getResources().getStringArray(R.array.units);
        String target = PreferencesHelper.UNIT_IMPERIAL.equals(preferred) ? "cup" : "g";
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(target)) {
                unitSpinner.setSelection(i);
                break;
            }
        }
    }

    private void loadItem() {
        PantryItem item = database.getPantryItem(editingId);
        if (item == null) {
            Toast.makeText(this, R.string.missing_item, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        nameInput.setText(item.getName());
        quantityInput.setText(String.valueOf(item.getQuantity()));
        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(item.getUnit())) {
                unitSpinner.setSelection(i);
                break;
            }
        }
        selectedExpiry = item.getExpiryDate();
        expiryText.setText(selectedExpiry == null || selectedExpiry.isEmpty()
                ? getString(R.string.no_expiry) : selectedExpiry);
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedExpiry = DateValidator.formatDisplay(year, month, dayOfMonth);
            expiryText.setText(selectedExpiry);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void save() {
        nameLayout.setError(null);
        quantityLayout.setError(null);

        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText() == null ? "" : quantityInput.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem() == null ? "" : unitSpinner.getSelectedItem().toString();

        boolean valid = true;
        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name));
            valid = false;
        }

        double quantity = 0;
        try {
            quantity = Double.parseDouble(quantityText);
            if (quantity <= 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
                quantityLayout.setError(getString(R.string.error_quantity));
                valid = false;
            }
        } catch (NumberFormatException e) {
            quantityLayout.setError(getString(R.string.error_quantity));
            valid = false;
        }

        if (unit.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_unit, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (selectedExpiry != null && !selectedExpiry.isEmpty() && !DateValidator.isValidIsoDate(selectedExpiry)) {
            Toast.makeText(this, R.string.error_expiry, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (!valid) {
            return;
        }

        PantryItem item = new PantryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(selectedExpiry);

        try {
            if (editingId > 0) {
                item.setId(editingId);
                int updated = database.updatePantryItem(item);
                if (updated == 0) {
                    Toast.makeText(this, R.string.missing_item, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                long id = database.insertPantryItem(item);
                if (id <= 0) {
                    Toast.makeText(this, "Could not save this ingredient.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save this ingredient.", Toast.LENGTH_LONG).show();
        }
    }
}
