package com.example.polygonpad.data.model

import java.math.BigDecimal

enum class PolygonCalculationMode(val label: String) {
    CALCULATE("Calculate"),
    FIND_SIDE("Find Side")
}

enum class FindSideMethod(val label: String) {
    FROM_PERIMETER("From Perimeter"),
    FROM_AREA("From Area")
}

enum class MeasurementUnit(
    val symbol: String,
    val displayName: String,
    val areaSymbol: String
) {
    MILLIMETER("mm", "Millimeters", "mm²"),
    CENTIMETER("cm", "Centimeters", "cm²"),
    METER("m", "Meters", "m²"),
    INCH("in", "Inches", "in²"),
    FOOT("ft", "Feet", "ft²");

    companion object {
        fun fromSymbol(symbol: String): MeasurementUnit {
            return entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: CENTIMETER
        }
    }
}

data class PolygonInput(
    val projectName: String = "",
    val calculationMode: PolygonCalculationMode = PolygonCalculationMode.CALCULATE,
    val findSideMethod: FindSideMethod = FindSideMethod.FROM_PERIMETER,
    val sides: Int = 6,
    val sideLengthMm: BigDecimal? = null,
    val perimeterMm: BigDecimal? = null,
    val areaMm2: BigDecimal? = null,
    val unit: MeasurementUnit = MeasurementUnit.CENTIMETER
)

data class PolygonResult(
    val sides: Int,
    val sideLengthMm: BigDecimal,
    val perimeterMm: BigDecimal,
    val areaMm2: BigDecimal,
    val apothemMm: BigDecimal,
    val circumradiusMm: BigDecimal,
    val interiorAngleDegrees: BigDecimal,
    val exteriorAngleDegrees: BigDecimal,
    val centralAngleDegrees: BigDecimal
) {
    val polygonName: String
        get() = getPolygonName(sides)

    companion object {
        fun getPolygonName(sides: Int): String {
            return when (sides) {
                3 -> "Triangle"
                4 -> "Square"
                5 -> "Pentagon"
                6 -> "Hexagon"
                7 -> "Heptagon"
                8 -> "Octagon"
                9 -> "Nonagon"
                10 -> "Decagon"
                11 -> "Undecagon"
                12 -> "Dodecagon"
                in 13..100 -> "$sides-sided polygon"
                else -> "Polygon"
            }
        }

        fun getFormalPolygonLabel(sides: Int): String {
            return when (sides) {
                3 -> "REGULAR TRIANGLE"
                4 -> "REGULAR SQUARE"
                5 -> "REGULAR PENTAGON"
                6 -> "REGULAR HEXAGON"
                7 -> "REGULAR HEPTAGON"
                8 -> "REGULAR OCTAGON"
                9 -> "REGULAR NONAGON"
                10 -> "REGULAR DECAGON"
                11 -> "REGULAR UNDECAGON"
                12 -> "REGULAR DODECAGON"
                in 13..100 -> "REGULAR $sides-SIDED POLYGON"
                else -> "REGULAR POLYGON"
            }
        }
    }
}

data class PolygonPreset(
    val name: String,
    val sides: Int
)

val COMMON_POLYGON_PRESETS = listOf(
    PolygonPreset("Triangle", 3),
    PolygonPreset("Square", 4),
    PolygonPreset("Pentagon", 5),
    PolygonPreset("Hexagon", 6),
    PolygonPreset("Octagon", 8),
    PolygonPreset("Decagon", 10),
    PolygonPreset("Dodecagon", 12)
)

data class SavedCalculation(
    val id: String,
    val projectName: String,
    val calculationMode: PolygonCalculationMode,
    val findSideMethod: FindSideMethod,
    val sides: Int,
    val sideLengthMm: BigDecimal,
    val perimeterMm: BigDecimal,
    val areaMm2: BigDecimal,
    val apothemMm: BigDecimal,
    val circumradiusMm: BigDecimal,
    val interiorAngleDegrees: BigDecimal,
    val exteriorAngleDegrees: BigDecimal,
    val centralAngleDegrees: BigDecimal,
    val unit: MeasurementUnit,
    val timestamp: Long,
    val isFavorite: Boolean = false
) {
    val polygonName: String
        get() = PolygonResult.getPolygonName(sides)

    val displayName: String
        get() = projectName.ifBlank { "Untitled Polygon" }
}

enum class AppTheme(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val defaultCalculationMode: PolygonCalculationMode = PolygonCalculationMode.CALCULATE,
    val defaultFindSideMethod: FindSideMethod = FindSideMethod.FROM_PERIMETER,
    val defaultUnit: MeasurementUnit = MeasurementUnit.CENTIMETER,
    val decimalPlaces: Int = 2,
    val showDiagram: Boolean = true,
    val showFormula: Boolean = true,
    val showAngles: Boolean = true,
    val showApothem: Boolean = true,
    val showCircumradius: Boolean = true,
    val hapticFeedback: Boolean = true,
    val confirmDelete: Boolean = true
)
