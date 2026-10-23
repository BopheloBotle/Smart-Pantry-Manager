package com.bophelobotle.blushpantry.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** Stores Settings values in SharedPreferences so they survive app restarts. */
public class PreferencesHelper {

    public static final String UNIT_METRIC = "metric";
    public static final String UNIT_IMPERIAL = "imperial";

    private static final String PREFS = "blush_pantry_prefs";
    private static final String KEY_REMINDERS = "expiry_reminders";
    private static final String KEY_UNITS = "preferred_units";

    private final SharedPreferences prefs;

    public PreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isExpiryRemindersEnabled() {
        return prefs.getBoolean(KEY_REMINDERS, true);
    }

    public void setExpiryRemindersEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REMINDERS, enabled).apply();
    }

    public String getPreferredUnits() {
        return prefs.getString(KEY_UNITS, UNIT_METRIC);
    }

    public void setPreferredUnits(String units) {
        prefs.edit().putString(KEY_UNITS, units).apply();
    }
}
