package com.bophelobotle.blushpantry.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateValidator {

    public static final String PATTERN = "yyyy-MM-dd";

    private DateValidator() {
    }

    public static boolean isValidIsoDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.US);
        format.setLenient(false);
        try {
            format.parse(value.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isExpiringSoon(String isoDate, int days) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.US);
        format.setLenient(false);
        try {
            Date expiry = format.parse(isoDate.trim());
            if (expiry == null) {
                return false;
            }
            Calendar limit = Calendar.getInstance();
            limit.add(Calendar.DAY_OF_YEAR, days);
            Calendar startOfToday = Calendar.getInstance();
            startOfToday.set(Calendar.HOUR_OF_DAY, 0);
            startOfToday.set(Calendar.MINUTE, 0);
            startOfToday.set(Calendar.SECOND, 0);
            startOfToday.set(Calendar.MILLISECOND, 0);
            return !expiry.before(startOfToday.getTime()) && !expiry.after(limit.getTime());
        } catch (ParseException e) {
            return false;
        }
    }

    public static String formatDisplay(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }
}
