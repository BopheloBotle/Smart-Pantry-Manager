package com.bophelobotle.blushpantry.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateValidatorTest {

    @Test
    public void emptyDateIsAllowed() {
        assertTrue(DateValidator.isValidIsoDate(null));
        assertTrue(DateValidator.isValidIsoDate(""));
    }

    @Test
    public void validIsoDate() {
        assertTrue(DateValidator.isValidIsoDate("2026-10-19"));
    }

    @Test
    public void invalidDatesAreRejected() {
        assertFalse(DateValidator.isValidIsoDate("19/10/2026"));
        assertFalse(DateValidator.isValidIsoDate("2026-13-40"));
        assertFalse(DateValidator.isValidIsoDate("not-a-date"));
    }
}
