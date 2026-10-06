package com.example.polygonpad.data.math

import com.example.polygonpad.data.model.MeasurementUnit
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object UnitConverter {
    val MATH_CONTEXT = MathContext(34, RoundingMode.HALF_UP)

    // Linear scale factors relative to 1 millimeter (mm)
    private val MM_FACTOR = BigDecimal("1.0")
    private val CM_FACTOR = BigDecimal("10.0")
    private val M_FACTOR = BigDecimal("1000.0")
    private val INCH_FACTOR = BigDecimal("25.4")
    private val FT_FACTOR = BigDecimal("304.8")

    // Area scale factors relative to 1 square millimeter (mm²)
    private val MM2_FACTOR = BigDecimal("1.0")
    private val CM2_FACTOR = BigDecimal("100.0")
    private val M2_FACTOR = BigDecimal("1000000.0")
    private val INCH2_FACTOR = BigDecimal("645.16")
    private val FT2_FACTOR = BigDecimal("92903.04")

    fun getLinearFactor(unit: MeasurementUnit): BigDecimal {
        return when (unit) {
            MeasurementUnit.MILLIMETER -> MM_FACTOR
            MeasurementUnit.CENTIMETER -> CM_FACTOR
            MeasurementUnit.METER -> M_FACTOR
            MeasurementUnit.INCH -> INCH_FACTOR
            MeasurementUnit.FOOT -> FT_FACTOR
        }
    }

    fun getAreaFactor(unit: MeasurementUnit): BigDecimal {
        return when (unit) {
            MeasurementUnit.MILLIMETER -> MM2_FACTOR
            MeasurementUnit.CENTIMETER -> CM2_FACTOR
            MeasurementUnit.METER -> M2_FACTOR
            MeasurementUnit.INCH -> INCH2_FACTOR
            MeasurementUnit.FOOT -> FT2_FACTOR
        }
    }

    /**
     * Converts a linear dimension in [fromUnit] to millimeters (mm).
     */
    fun toMillimeters(value: BigDecimal, fromUnit: MeasurementUnit): BigDecimal {
        return value.multiply(getLinearFactor(fromUnit), MATH_CONTEXT)
    }

    /**
     * Converts a linear dimension in millimeters (mm) to [toUnit].
     */
    fun fromMillimeters(valueMm: BigDecimal, toUnit: MeasurementUnit): BigDecimal {
        val factor = getLinearFactor(toUnit)
        return valueMm.divide(factor, MATH_CONTEXT)
    }

    /**
     * Converts an area in [fromUnit] to square millimeters (mm²).
     */
    fun toSquareMillimeters(value: BigDecimal, fromUnit: MeasurementUnit): BigDecimal {
        return value.multiply(getAreaFactor(fromUnit), MATH_CONTEXT)
    }

    /**
     * Converts an area in square millimeters (mm²) to [toUnit].
     */
    fun fromSquareMillimeters(valueMm2: BigDecimal, toUnit: MeasurementUnit): BigDecimal {
        val factor = getAreaFactor(toUnit)
        return valueMm2.divide(factor, MATH_CONTEXT)
    }

    /**
     * Preserves physical measurement when switching units:
     * Converts value from oldUnit to newUnit.
     */
    fun convertLinear(value: BigDecimal, fromUnit: MeasurementUnit, toUnit: MeasurementUnit): BigDecimal {
        if (fromUnit == toUnit) return value
        val mm = toMillimeters(value, fromUnit)
        return fromMillimeters(mm, toUnit)
    }

    fun convertArea(value: BigDecimal, fromUnit: MeasurementUnit, toUnit: MeasurementUnit): BigDecimal {
        if (fromUnit == toUnit) return value
        val mm2 = toSquareMillimeters(value, fromUnit)
        return fromSquareMillimeters(mm2, toUnit)
    }
}
