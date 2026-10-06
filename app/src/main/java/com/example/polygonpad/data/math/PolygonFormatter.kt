package com.example.polygonpad.data.math

import com.example.polygonpad.data.model.MeasurementUnit
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PolygonFormatter {

    fun formatNumber(value: BigDecimal, decimalPlaces: Int = 2): String {
        val scaled = value.setScale(decimalPlaces, RoundingMode.HALF_UP)
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = if (decimalPlaces <= 0) {
            "#,##0"
        } else {
            val zeros = "0".repeat(decimalPlaces)
            val optionalZeros = "#".repeat(decimalPlaces)
            "#,##0.$optionalZeros"
        }
        val df = DecimalFormat(pattern, symbols)
        val formatted = df.format(scaled)
        // If it ends with . or .00 when optional, DecimalFormat handles it cleanly
        return if (formatted.isEmpty()) "0" else formatted
    }

    /**
     * Formats a raw number with exact fixed decimal places (e.g. 259.81).
     */
    fun formatFixedNumber(value: BigDecimal, decimalPlaces: Int = 2): String {
        val scaled = value.setScale(decimalPlaces, RoundingMode.HALF_UP)
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = if (decimalPlaces <= 0) {
            "#,##0"
        } else {
            "#,##0." + "0".repeat(decimalPlaces)
        }
        val df = DecimalFormat(pattern, symbols)
        return df.format(scaled)
    }

    /**
     * Formats a linear dimension in millimeters to the target unit and displays it.
     */
    fun formatDimension(valueMm: BigDecimal, unit: MeasurementUnit, decimalPlaces: Int = 2): String {
        val converted = UnitConverter.fromMillimeters(valueMm, unit)
        val numStr = formatNumber(converted, decimalPlaces)
        return "$numStr ${unit.symbol}"
    }

    /**
     * Formats an area in square millimeters to the target unit and displays it.
     */
    fun formatArea(valueMm2: BigDecimal, unit: MeasurementUnit, decimalPlaces: Int = 2): String {
        val converted = UnitConverter.fromSquareMillimeters(valueMm2, unit)
        val numStr = formatNumber(converted, decimalPlaces)
        return "$numStr ${unit.areaSymbol}"
    }

    /**
     * Formats an angle in degrees.
     */
    fun formatAngle(degrees: BigDecimal, decimalPlaces: Int = 2): String {
        val numStr = formatNumber(degrees, decimalPlaces)
        return "$numStr°"
    }

    /**
     * Formats the result copy/share text according to specification.
     */
    fun formatShareText(
        sides: Int,
        sideLengthMm: BigDecimal,
        areaMm2: BigDecimal,
        perimeterMm: BigDecimal,
        apothemMm: BigDecimal,
        circumradiusMm: BigDecimal,
        interiorAngle: BigDecimal,
        exteriorAngle: BigDecimal,
        centralAngle: BigDecimal,
        unit: MeasurementUnit,
        projectName: String? = null,
        decimalPlaces: Int = 2
    ): String {
        val polygonName = com.example.polygonpad.data.model.PolygonResult.getPolygonName(sides)
        val title = if (!projectName.isNullOrBlank()) "$projectName\n" else ""
        return buildString {
            append("PolygonPad\n\n")
            if (title.isNotEmpty()) {
                append(title)
            }
            append("Regular $polygonName\n")
            append("Sides: $sides\n")
            append("Side Length: ${formatDimension(sideLengthMm, unit, decimalPlaces)}\n")
            append("Area: ${formatArea(areaMm2, unit, decimalPlaces)}\n")
            append("Perimeter: ${formatDimension(perimeterMm, unit, decimalPlaces)}\n")
            append("Apothem: ${formatDimension(apothemMm, unit, decimalPlaces)}\n")
            append("Circumradius: ${formatDimension(circumradiusMm, unit, decimalPlaces)}\n")
            append("Interior Angle: ${formatAngle(interiorAngle, decimalPlaces)}\n")
            append("Exterior Angle: ${formatAngle(exteriorAngle, decimalPlaces)}\n")
            append("Central Angle: ${formatAngle(centralAngle, decimalPlaces)}")
        }
    }
}
