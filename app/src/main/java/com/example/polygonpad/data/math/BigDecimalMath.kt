package com.example.polygonpad.data.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {
    val MC = MathContext(34, RoundingMode.HALF_UP)

    // High precision Pi (35 digits)
    val PI = BigDecimal("3.14159265358979323846264338327950288")
    val TWO = BigDecimal("2")
    val THREE = BigDecimal("3")
    val FOUR = BigDecimal("4")
    val ONE_EIGHTY = BigDecimal("180")
    val THREE_SIXTY = BigDecimal("360")

    /**
     * High precision square root using Newton-Raphson iteration.
     * Safe across all Android API levels.
     */
    fun squareRoot(value: BigDecimal, mc: MathContext = MC): BigDecimal {
        require(value >= BigDecimal.ZERO) { "Cannot calculate square root of a negative number: $value" }
        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

        // Initial guess using Double
        var x = BigDecimal(kotlin.math.sqrt(value.toDouble()), mc)
        if (x.compareTo(BigDecimal.ZERO) == 0) {
            x = BigDecimal.ONE
        }

        val two = TWO
        val maxIterations = 30
        for (i in 0 until maxIterations) {
            val nextX = x.add(value.divide(x, mc), mc).divide(two, mc)
            if (nextX.subtract(x).abs().compareTo(BigDecimal("1E-30")) <= 0) {
                return nextX
            }
            x = nextX
        }
        return x
    }

    /**
     * High-precision Taylor series sine for radians in range [0, PI].
     * sin(x) = x - x^3/3! + x^5/5! - x^7/7! + ...
     */
    fun sin(radians: BigDecimal, mc: MathContext = MC): BigDecimal {
        // Normalize radians to [0, 2*PI)
        val twoPi = PI.multiply(TWO, mc)
        var x = radians.remainder(twoPi, mc)
        if (x < BigDecimal.ZERO) {
            x = x.add(twoPi, mc)
        }

        var term = x
        var sum = x
        val xSquared = x.multiply(x, mc)

        var n = 1
        var sign = -1

        for (i in 1..25) {
            val denom = BigDecimal((2 * n) * (2 * n + 1))
            term = term.multiply(xSquared, mc).divide(denom, mc)
            val signedTerm = if (sign < 0) term.negate() else term
            sum = sum.add(signedTerm, mc)
            if (term.abs().compareTo(BigDecimal("1E-32")) <= 0) break
            sign = -sign
            n++
        }
        return sum
    }

    /**
     * High-precision Taylor series cosine for radians.
     * cos(x) = 1 - x^2/2! + x^4/4! - x^6/6! + ...
     */
    fun cos(radians: BigDecimal, mc: MathContext = MC): BigDecimal {
        val twoPi = PI.multiply(TWO, mc)
        var x = radians.remainder(twoPi, mc)
        if (x < BigDecimal.ZERO) {
            x = x.add(twoPi, mc)
        }

        var term = BigDecimal.ONE
        var sum = BigDecimal.ONE
        val xSquared = x.multiply(x, mc)

        var n = 1
        var sign = -1

        for (i in 1..25) {
            val denom = BigDecimal((2 * n - 1) * (2 * n))
            term = term.multiply(xSquared, mc).divide(denom, mc)
            val signedTerm = if (sign < 0) term.negate() else term
            sum = sum.add(signedTerm, mc)
            if (term.abs().compareTo(BigDecimal("1E-32")) <= 0) break
            sign = -sign
            n++
        }
        return sum
    }

    /**
     * High-precision tangent: tan(x) = sin(x) / cos(x).
     */
    fun tan(radians: BigDecimal, mc: MathContext = MC): BigDecimal {
        val s = sin(radians, mc)
        val c = cos(radians, mc)
        require(c.abs().compareTo(BigDecimal("1E-30")) > 0) { "Tangent undefined for radians: $radians" }
        return s.divide(c, mc)
    }
}
