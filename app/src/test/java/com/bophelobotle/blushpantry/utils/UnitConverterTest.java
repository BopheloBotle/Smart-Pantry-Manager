package com.bophelobotle.blushpantry.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnitConverterTest {

    @Test
    public void gramsToKilograms() {
        assertEquals(1.0, UnitConverter.convert(1000, "g", "kg"), 0.0001);
    }

    @Test
    public void millilitresToLitres() {
        assertEquals(0.5, UnitConverter.convert(500, "ml", "l"), 0.0001);
    }

    @Test
    public void teaspoonsToMillilitres() {
        assertEquals(15.0, UnitConverter.convert(3, "tsp", "ml"), 0.0001);
    }

    @Test
    public void incompatibleUnitsReturnNaN() {
        assertTrue(Double.isNaN(UnitConverter.convert(2, "piece", "g")));
        assertTrue(Double.isNaN(UnitConverter.convert(1, "cup", "piece")));
    }

    @Test
    public void differentCountSubtypesAreNotEquivalent() {
        assertTrue(Double.isNaN(UnitConverter.convert(2, "slice", "piece")));
        assertTrue(Double.isNaN(UnitConverter.convert(2, "clove", "piece")));
    }
}
