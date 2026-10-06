package com.example.polygonpad.data.math

import com.example.polygonpad.data.model.PolygonResult
import java.math.BigDecimal

object PolygonPadCalculator {
    const val MIN_SIDES = 3
    const val MAX_SIDES = 100

    fun validateSides(sides: Int) {
        require(sides in MIN_SIDES..MAX_SIDES) {
            "Enter a whole number of sides from $MIN_SIDES to $MAX_SIDES."
        }
    }

    fun validatePositive(value: BigDecimal, fieldName: String) {
        require(value > BigDecimal.ZERO) {
            "$fieldName must be greater than zero."
        }
    }

    /**
     * P = n * s
     */
    fun calculatePerimeter(sides: Int, sideLengthMm: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(sideLengthMm, "Side length")
        return sideLengthMm.multiply(BigDecimal(sides), BigDecimalMath.MC)
    }

    /**
     * Interior Angle = ((n - 2) * 180) / n
     */
    fun calculateInteriorAngle(sides: Int): BigDecimal {
        validateSides(sides)
        val n = BigDecimal(sides)
        val nMinus2 = BigDecimal(sides - 2)
        return nMinus2.multiply(BigDecimalMath.ONE_EIGHTY, BigDecimalMath.MC)
            .divide(n, BigDecimalMath.MC)
    }

    /**
     * Exterior Angle = 360 / n
     */
    fun calculateExteriorAngle(sides: Int): BigDecimal {
        validateSides(sides)
        return BigDecimalMath.THREE_SIXTY.divide(BigDecimal(sides), BigDecimalMath.MC)
    }

    /**
     * Central Angle = 360 / n
     */
    fun calculateCentralAngle(sides: Int): BigDecimal {
        validateSides(sides)
        return BigDecimalMath.THREE_SIXTY.divide(BigDecimal(sides), BigDecimalMath.MC)
    }

    /**
     * Apothem a = s / (2 * tan(pi / n))
     */
    fun calculateApothem(sides: Int, sideLengthMm: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(sideLengthMm, "Side length")
        val piOverN = BigDecimalMath.PI.divide(BigDecimal(sides), BigDecimalMath.MC)
        val tanPiOverN = BigDecimalMath.tan(piOverN, BigDecimalMath.MC)
        val denom = BigDecimalMath.TWO.multiply(tanPiOverN, BigDecimalMath.MC)
        return sideLengthMm.divide(denom, BigDecimalMath.MC)
    }

    /**
     * Circumradius R = s / (2 * sin(pi / n))
     */
    fun calculateCircumradius(sides: Int, sideLengthMm: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(sideLengthMm, "Side length")
        val piOverN = BigDecimalMath.PI.divide(BigDecimal(sides), BigDecimalMath.MC)
        val sinPiOverN = BigDecimalMath.sin(piOverN, BigDecimalMath.MC)
        val denom = BigDecimalMath.TWO.multiply(sinPiOverN, BigDecimalMath.MC)
        return sideLengthMm.divide(denom, BigDecimalMath.MC)
    }

    /**
     * Area A = (n * s^2) / (4 * tan(pi / n))
     * Computed directly from inputs with full internal precision.
     */
    fun calculateArea(sides: Int, sideLengthMm: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(sideLengthMm, "Side length")
        val n = BigDecimal(sides)
        val sSquared = sideLengthMm.multiply(sideLengthMm, BigDecimalMath.MC)
        val numerator = n.multiply(sSquared, BigDecimalMath.MC)

        val piOverN = BigDecimalMath.PI.divide(n, BigDecimalMath.MC)
        val tanPiOverN = BigDecimalMath.tan(piOverN, BigDecimalMath.MC)
        val denominator = BigDecimalMath.FOUR.multiply(tanPiOverN, BigDecimalMath.MC)

        return numerator.divide(denominator, BigDecimalMath.MC)
    }

    /**
     * Side from Perimeter: s = perimeter / n
     */
    fun calculateSideFromPerimeter(sides: Int, perimeterMm: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(perimeterMm, "Perimeter")
        val side = perimeterMm.divide(BigDecimal(sides), BigDecimalMath.MC)
        validatePositive(side, "Calculated side")
        return side
    }

    /**
     * Side from Area: s = sqrt((4 * Area * tan(pi / n)) / n)
     */
    fun calculateSideFromArea(sides: Int, areaMm2: BigDecimal): BigDecimal {
        validateSides(sides)
        validatePositive(areaMm2, "Area")
        val n = BigDecimal(sides)
        val piOverN = BigDecimalMath.PI.divide(n, BigDecimalMath.MC)
        val tanPiOverN = BigDecimalMath.tan(piOverN, BigDecimalMath.MC)

        val fourAreaTan = BigDecimalMath.FOUR
            .multiply(areaMm2, BigDecimalMath.MC)
            .multiply(tanPiOverN, BigDecimalMath.MC)

        val sSquared = fourAreaTan.divide(n, BigDecimalMath.MC)
        require(sSquared > BigDecimal.ZERO) { "Unable to calculate a valid side length." }

        val side = BigDecimalMath.squareRoot(sSquared, BigDecimalMath.MC)
        validatePositive(side, "Calculated side")
        return side
    }

    /**
     * Main calculation function:
     * Calculates all polygon properties using full internal precision.
     */
    fun calculatePolygon(sides: Int, sideLengthMm: BigDecimal): PolygonResult {
        validateSides(sides)
        validatePositive(sideLengthMm, "Side length")

        val perimeter = calculatePerimeter(sides, sideLengthMm)
        val area = calculateArea(sides, sideLengthMm)
        val apothem = calculateApothem(sides, sideLengthMm)
        val circumradius = calculateCircumradius(sides, sideLengthMm)
        val interiorAngle = calculateInteriorAngle(sides)
        val exteriorAngle = calculateExteriorAngle(sides)
        val centralAngle = calculateCentralAngle(sides)

        return PolygonResult(
            sides = sides,
            sideLengthMm = sideLengthMm,
            perimeterMm = perimeter,
            areaMm2 = area,
            apothemMm = apothem,
            circumradiusMm = circumradius,
            interiorAngleDegrees = interiorAngle,
            exteriorAngleDegrees = exteriorAngle,
            centralAngleDegrees = centralAngle
        )
    }
}
