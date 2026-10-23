package com.bophelobotle.blushpantry.utils;

/**
 * Converts compatible kitchen units so recipe matching can compare quantities.
 *
 * Compatible families:
 * - MASS: mg, g, kg, oz, lb
 * - VOLUME: ml, l, tsp, tbsp, cup, fl oz
 * - COUNT: piece, slice, clove, each
 *
 * Incompatible units (for example grams vs pieces) are never converted.
 * The matcher then treats that ingredient as not matched.
 */
public final class UnitConverter {

    public enum Family {
        MASS, VOLUME, COUNT, UNKNOWN
    }

    private UnitConverter() {
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String value = unit.trim().toLowerCase();
        if (value.endsWith("s") && value.length() > 2 && !value.equals("cups")) {
            // pieces -> piece, cloves -> clove, slices -> slice
            if (value.equals("pieces") || value.equals("cloves") || value.equals("slices")
                    || value.equals("grams") || value.equals("kilograms")
                    || value.equals("litres") || value.equals("liters")
                    || value.equals("teaspoons") || value.equals("tablespoons")) {
                value = value.substring(0, value.length() - 1);
            }
        }
        switch (value) {
            case "gram":
            case "grams":
                return "g";
            case "kilogram":
            case "kilograms":
                return "kg";
            case "millilitre":
            case "milliliter":
            case "millilitres":
            case "milliliters":
                return "ml";
            case "litre":
            case "liter":
            case "litres":
            case "liters":
                return "l";
            case "teaspoon":
            case "teaspoons":
                return "tsp";
            case "tablespoon":
            case "tablespoons":
                return "tbsp";
            case "cups":
                return "cup";
            case "ounce":
            case "ounces":
                return "oz";
            case "pound":
            case "pounds":
                return "lb";
            case "pcs":
            case "pc":
            case "each":
            case "unit":
            case "units":
            case "item":
            case "items":
            case "whole":
            case "count":
                return "piece";
            default:
                return value;
        }
    }

    public static Family familyOf(String unit) {
        switch (normalizeUnit(unit)) {
            case "mg":
            case "g":
            case "kg":
            case "oz":
            case "lb":
                return Family.MASS;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
            case "fl oz":
            case "floz":
                return Family.VOLUME;
            case "piece":
            case "slice":
            case "clove":
                return Family.COUNT;
            default:
                return Family.UNKNOWN;
        }
    }

    /**
     * Converts a quantity into a canonical amount inside its family.
     * MASS canonical unit is grams. VOLUME canonical unit is millilitres.
     * COUNT stays as the original count (piece, slice, and clove are NOT mixed).
     *
     * @return converted amount, or NaN if the unit is unknown
     */
    public static double toCanonical(double quantity, String unit) {
        String normalized = normalizeUnit(unit);
        switch (normalized) {
            case "mg":
                return quantity * 0.001;
            case "g":
                return quantity;
            case "kg":
                return quantity * 1000.0;
            case "oz":
                return quantity * 28.3495;
            case "lb":
                return quantity * 453.592;
            case "ml":
                return quantity;
            case "l":
                return quantity * 1000.0;
            case "tsp":
                return quantity * 5.0;
            case "tbsp":
                return quantity * 15.0;
            case "cup":
                return quantity * 240.0;
            case "fl oz":
            case "floz":
                return quantity * 29.5735;
            case "piece":
            case "slice":
            case "clove":
                return quantity;
            default:
                return Double.NaN;
        }
    }

    /**
     * Converts pantryQuantity from pantryUnit into recipeUnit.
     * Returns NaN when the units belong to different families, when a COUNT
     * subtype differs (slice vs piece), or when a unit is unknown.
     */
    public static double convert(double pantryQuantity, String pantryUnit, String recipeUnit) {
        String from = normalizeUnit(pantryUnit);
        String to = normalizeUnit(recipeUnit);
        Family fromFamily = familyOf(from);
        Family toFamily = familyOf(to);

        if (fromFamily == Family.UNKNOWN || toFamily == Family.UNKNOWN || fromFamily != toFamily) {
            return Double.NaN;
        }

        // Count units are only compatible when they are the same subtype.
        if (fromFamily == Family.COUNT && !from.equals(to)) {
            return Double.NaN;
        }

        if (from.equals(to)) {
            return pantryQuantity;
        }

        double canonical = toCanonical(pantryQuantity, from);
        double recipeCanonicalPerUnit = toCanonical(1.0, to);
        if (Double.isNaN(canonical) || Double.isNaN(recipeCanonicalPerUnit) || recipeCanonicalPerUnit == 0) {
            return Double.NaN;
        }
        return canonical / recipeCanonicalPerUnit;
    }
}
