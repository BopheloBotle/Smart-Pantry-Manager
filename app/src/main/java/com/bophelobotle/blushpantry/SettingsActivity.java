package com.bophelobotle.blushpantry;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bophelobotle.blushpantry.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_title);

        PreferencesHelper prefs = new PreferencesHelper(this);
        SwitchCompat reminders = findViewById(R.id.switch_reminders);
        RadioGroup units = findViewById(R.id.group_units);

        reminders.setChecked(prefs.isExpiryRemindersEnabled());
        if (PreferencesHelper.UNIT_IMPERIAL.equals(prefs.getPreferredUnits())) {
            units.check(R.id.radio_imperial);
        } else {
            units.check(R.id.radio_metric);
        }

        reminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setExpiryRemindersEnabled(isChecked);
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });
        units.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_imperial) {
                prefs.setPreferredUnits(PreferencesHelper.UNIT_IMPERIAL);
            } else {
                prefs.setPreferredUnits(PreferencesHelper.UNIT_METRIC);
            }
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });

        NavigationHelper.bind(this, R.id.nav_settings);
    }
}
