package com.example.polygonpad

import com.example.polygonpad.data.math.BigDecimalMath
import com.example.polygonpad.data.math.PolygonFormatter
import com.example.polygonpad.data.math.PolygonPadCalculator
import com.example.polygonpad.data.math.UnitConverter
import com.example.polygonpad.data.model.AppSettings
import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.data.model.SavedCalculation
import com.example.polygonpad.data.preferences.CalculationSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class PolygonPadUnitTest {

    private val tenCmInMm = BigDecimal("100.0") // 10 cm = 100 mm

    // 1. Triangle
    @Test
    fun test01_equilateralTriangle() {
        val result = PolygonPadCalculator.calculatePolygon(3, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)
        val circumradiusCm = UnitConverter.fromMillimeters(result.circumradiusMm, MeasurementUnit.CENTIMETER)

        assertEquals(30.0, perimCm.toDouble(), 0.001)
        assertEquals(60.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(120.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(120.0, result.centralAngleDegrees.toDouble(), 0.001)
        assertEquals(2.8868, apothemCm.toDouble(), 0.001)
        assertEquals(5.7735, circumradiusCm.toDouble(), 0.001)
        assertEquals(43.3013, areaCm2.toDouble(), 0.001)
    }

    // 2. Square
    @Test
    fun test02_square() {
        val result = PolygonPadCalculator.calculatePolygon(4, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)
        val circumradiusCm = UnitConverter.fromMillimeters(result.circumradiusMm, MeasurementUnit.CENTIMETER)

        assertEquals(40.0, perimCm.toDouble(), 0.001)
        assertEquals(90.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(90.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(90.0, result.centralAngleDegrees.toDouble(), 0.001)
        assertEquals(5.0, apothemCm.toDouble(), 0.001)
        assertEquals(7.0711, circumradiusCm.toDouble(), 0.001)
        assertEquals(100.0, areaCm2.toDouble(), 0.001)
    }

    // 3. Pentagon
    @Test
    fun test03_pentagon() {
        val result = PolygonPadCalculator.calculatePolygon(5, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)

        assertEquals(50.0, perimCm.toDouble(), 0.001)
        assertEquals(108.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(72.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(6.8819, apothemCm.toDouble(), 0.001)
        assertEquals(172.0477, areaCm2.toDouble(), 0.001)
    }

    // 4. Hexagon
    @Test
    fun test04_hexagon() {
        val result = PolygonPadCalculator.calculatePolygon(6, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)
        val circumradiusCm = UnitConverter.fromMillimeters(result.circumradiusMm, MeasurementUnit.CENTIMETER)

        assertEquals(60.0, perimCm.toDouble(), 0.001)
        assertEquals(120.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(60.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(60.0, result.centralAngleDegrees.toDouble(), 0.001)
        assertEquals(8.6603, apothemCm.toDouble(), 0.001)
        assertEquals(10.0, circumradiusCm.toDouble(), 0.001)
        assertEquals(259.8076, areaCm2.toDouble(), 0.001)
    }

    // 5. Octagon
    @Test
    fun test05_octagon() {
        val result = PolygonPadCalculator.calculatePolygon(8, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)

        assertEquals(80.0, perimCm.toDouble(), 0.001)
        assertEquals(135.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(45.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(12.0711, apothemCm.toDouble(), 0.001)
        assertEquals(482.8427, areaCm2.toDouble(), 0.001)
    }

    // 6. Decagon
    @Test
    fun test06_decagon() {
        val result = PolygonPadCalculator.calculatePolygon(10, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)

        assertEquals(100.0, perimCm.toDouble(), 0.001)
        assertEquals(144.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(36.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(15.3884, apothemCm.toDouble(), 0.001)
        assertEquals(769.4209, areaCm2.toDouble(), 0.001)
    }

    // 7. Dodecagon
    @Test
    fun test07_dodecagon() {
        val result = PolygonPadCalculator.calculatePolygon(12, tenCmInMm)
        val perimCm = UnitConverter.fromMillimeters(result.perimeterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)
        val apothemCm = UnitConverter.fromMillimeters(result.apothemMm, MeasurementUnit.CENTIMETER)

        assertEquals(120.0, perimCm.toDouble(), 0.001)
        assertEquals(150.0, result.interiorAngleDegrees.toDouble(), 0.001)
        assertEquals(30.0, result.exteriorAngleDegrees.toDouble(), 0.001)
        assertEquals(18.6603, apothemCm.toDouble(), 0.001)
        assertEquals(1119.6152, areaCm2.toDouble(), 0.001)
    }

    // 8. Perimeter
    @Test
    fun test08_perimeter() {
        val p = PolygonPadCalculator.calculatePerimeter(7, BigDecimal("25.0"))
        assertEquals(0, p.compareTo(BigDecimal("175.0")))
    }

    // 9. Area
    @Test
    fun test09_area() {
        // Square 100mm -> 10000 mm²
        val a = PolygonPadCalculator.calculateArea(4, BigDecimal("100.0"))
        assertEquals(0, a.setScale(4, RoundingMode.HALF_UP).compareTo(BigDecimal("10000.0000")))
    }

    // 10. Apothem
    @Test
    fun test10_apothem() {
        val ap = PolygonPadCalculator.calculateApothem(4, BigDecimal("100.0"))
        assertEquals(50.0, ap.toDouble(), 0.0001)
    }

    // 11. Circumradius
    @Test
    fun test11_circumradius() {
        val r = PolygonPadCalculator.calculateCircumradius(6, BigDecimal("100.0"))
        // For regular hexagon, circumradius == side length!
        assertEquals(100.0, r.toDouble(), 0.0001)
    }

    // 12. Interior angle
    @Test
    fun test12_interiorAngle() {
        val angle = PolygonPadCalculator.calculateInteriorAngle(8)
        assertEquals(0, angle.compareTo(BigDecimal("135")))
    }

    // 13. Exterior angle
    @Test
    fun test13_exteriorAngle() {
        val angle = PolygonPadCalculator.calculateExteriorAngle(5)
        assertEquals(0, angle.compareTo(BigDecimal("72")))
    }

    // 14. Central angle
    @Test
    fun test14_centralAngle() {
        val angle = PolygonPadCalculator.calculateCentralAngle(6)
        assertEquals(0, angle.compareTo(BigDecimal("60")))
    }

    // 15. Side from perimeter
    @Test
    fun test15_sideFromPerimeter() {
        val side = PolygonPadCalculator.calculateSideFromPerimeter(6, BigDecimal("600.0"))
        assertEquals(0, side.compareTo(BigDecimal("100")))
    }

    // 16. Side from area
    @Test
    fun test16_sideFromArea() {
        // Hexagon area with side 100mm is ~25980.7621135 mm²
        val originalResult = PolygonPadCalculator.calculatePolygon(6, BigDecimal("100.0"))
        val calculatedSide = PolygonPadCalculator.calculateSideFromArea(6, originalResult.areaMm2)
        assertEquals(100.0, calculatedSide.toDouble(), 0.0001)
    }

    // 17. Minimum sides
    @Test
    fun test17_minimumSides() {
        val result = PolygonPadCalculator.calculatePolygon(3, BigDecimal("50.0"))
        assertNotNull(result)
        assertEquals(3, result.sides)
    }

    // 18. Maximum sides
    @Test
    fun test18_maximumSides() {
        val result = PolygonPadCalculator.calculatePolygon(100, BigDecimal("50.0"))
        assertNotNull(result)
        assertEquals(100, result.sides)
    }

    // 19. Invalid side count
    @Test
    fun test19_invalidSideCount() {
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(2, BigDecimal("50.0"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(0, BigDecimal("50.0"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(-5, BigDecimal("50.0"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(101, BigDecimal("50.0"))
        }
    }

    // 20. Zero side length
    @Test
    fun test20_zeroSideLength() {
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(6, BigDecimal.ZERO)
        }
    }

    // 21. Negative side length
    @Test
    fun test21_negativeSideLength() {
        assertThrows(IllegalArgumentException::class.java) {
            PolygonPadCalculator.calculatePolygon(6, BigDecimal("-10.0"))
        }
    }

    // 22. Unit conversion
    @Test
    fun test22_unitConversion() {
        val cm = BigDecimal("10.0")
        val mm = UnitConverter.toMillimeters(cm, MeasurementUnit.CENTIMETER)
        assertEquals(0, mm.compareTo(BigDecimal("100.0")))

        val m = UnitConverter.fromMillimeters(mm, MeasurementUnit.METER)
        assertEquals(0, m.compareTo(BigDecimal("0.1")))

        val inches = UnitConverter.fromMillimeters(BigDecimal("25.4"), MeasurementUnit.INCH)
        assertEquals(0, inches.compareTo(BigDecimal("1.0")))

        val feet = UnitConverter.fromMillimeters(BigDecimal("304.8"), MeasurementUnit.FOOT)
        assertEquals(0, feet.compareTo(BigDecimal("1.0")))
    }

    // 23. Area unit conversion
    @Test
    fun test23_areaUnitConversion() {
        val cm2 = BigDecimal("100.0")
        val mm2 = UnitConverter.toSquareMillimeters(cm2, MeasurementUnit.CENTIMETER)
        assertEquals(0, mm2.compareTo(BigDecimal("10000.0")))

        val m2 = UnitConverter.fromSquareMillimeters(mm2, MeasurementUnit.METER)
        assertEquals(0, m2.compareTo(BigDecimal("0.01")))
    }

    // 24. Decimal dimensions
    @Test
    fun test24_decimalDimensions() {
        val side = BigDecimal("12.75")
        val result = PolygonPadCalculator.calculatePolygon(6, side)
        val expectedPerimeter = side.multiply(BigDecimal(6), BigDecimalMath.MC)
        assertEquals(0, result.perimeterMm.compareTo(expectedPerimeter))
    }

    // 25. BigDecimal precision
    @Test
    fun test25_bigDecimalPrecision() {
        val result = PolygonPadCalculator.calculatePolygon(6, BigDecimal("100.0"))
        // Check that intermediate values retain high precision (> 15 digits)
        assertTrue(result.areaMm2.toPlainString().length > 15)
        assertTrue(result.apothemMm.toPlainString().length > 15)
        // Verify formatted output does not have NaN or floating artifacts
        val formatted = PolygonFormatter.formatArea(result.areaMm2, MeasurementUnit.CENTIMETER, 2)
        assertEquals("259.81 cm²", formatted)
    }

    // 26. History serialization
    @Test
    fun test26_historySerialization() {
        val result = PolygonPadCalculator.calculatePolygon(6, BigDecimal("100.0"))
        val item = SavedCalculation(
            id = "test-1",
            projectName = "Hexagonal Table",
            calculationMode = PolygonCalculationMode.CALCULATE,
            findSideMethod = FindSideMethod.FROM_PERIMETER,
            sides = result.sides,
            sideLengthMm = result.sideLengthMm,
            perimeterMm = result.perimeterMm,
            areaMm2 = result.areaMm2,
            apothemMm = result.apothemMm,
            circumradiusMm = result.circumradiusMm,
            interiorAngleDegrees = result.interiorAngleDegrees,
            exteriorAngleDegrees = result.exteriorAngleDegrees,
            centralAngleDegrees = result.centralAngleDegrees,
            unit = MeasurementUnit.CENTIMETER,
            timestamp = 1700000000000L,
            isFavorite = true
        )

        val json = CalculationSerializer.toJson(listOf(item))
        val deserialized = CalculationSerializer.fromJson(json)

        assertEquals(1, deserialized.size)
        val restored = deserialized[0]
        assertEquals("test-1", restored.id)
        assertEquals("Hexagonal Table", restored.projectName)
        assertEquals(6, restored.sides)
        assertTrue(restored.isFavorite)
        assertEquals(0, restored.areaMm2.compareTo(result.areaMm2))
    }

    // 27. Settings persistence defaults
    @Test
    fun test27_settingsPersistence() {
        val defaultSettings = AppSettings()
        assertEquals(2, defaultSettings.decimalPlaces)
        assertTrue(defaultSettings.showDiagram)
        assertTrue(defaultSettings.showFormula)
        assertEquals(MeasurementUnit.CENTIMETER, defaultSettings.defaultUnit)
        assertEquals(PolygonCalculationMode.CALCULATE, defaultSettings.defaultCalculationMode)
    }

    // 28. Favorites
    @Test
    fun test28_favorites() {
        val item1 = SavedCalculation(
            id = "1",
            projectName = "First",
            calculationMode = PolygonCalculationMode.CALCULATE,
            findSideMethod = FindSideMethod.FROM_PERIMETER,
            sides = 4,
            sideLengthMm = BigDecimal("100.0"),
            perimeterMm = BigDecimal("400.0"),
            areaMm2 = BigDecimal("10000.0"),
            apothemMm = BigDecimal("50.0"),
            circumradiusMm = BigDecimal("70.71"),
            interiorAngleDegrees = BigDecimal("90.0"),
            exteriorAngleDegrees = BigDecimal("90.0"),
            centralAngleDegrees = BigDecimal("90.0"),
            unit = MeasurementUnit.CENTIMETER,
            timestamp = 1000L,
            isFavorite = false
        )
        val item2 = item1.copy(id = "2", projectName = "Second", timestamp = 2000L, isFavorite = true)

        val list = listOf(item1, item2)
        // Sort: favorites first, then newest
        val sorted = list.sortedWith(
            compareByDescending<SavedCalculation> { it.isFavorite }
                .thenByDescending { it.timestamp }
        )

        assertEquals("Second", sorted[0].projectName)
        assertTrue(sorted[0].isFavorite)
        assertEquals("First", sorted[1].projectName)
        assertFalse(sorted[1].isFavorite)
    }
}
