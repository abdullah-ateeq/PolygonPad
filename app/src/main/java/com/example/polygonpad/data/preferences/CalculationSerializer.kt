package com.example.polygonpad.data.preferences

import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.data.model.SavedCalculation
import java.math.BigDecimal

object CalculationSerializer {

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun unescape(s: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\\' && i + 1 < s.length) {
                when (val next = s[i + 1]) {
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    else -> sb.append(next)
                }
                i += 2
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }

    fun toJson(calculations: List<SavedCalculation>): String {
        val items = calculations.joinToString(",") { calc ->
            """{"id":"${escape(calc.id)}","projectName":"${escape(calc.projectName)}","calculationMode":"${calc.calculationMode.name}","findSideMethod":"${calc.findSideMethod.name}","sides":${calc.sides},"sideLengthMm":"${calc.sideLengthMm.toPlainString()}","perimeterMm":"${calc.perimeterMm.toPlainString()}","areaMm2":"${calc.areaMm2.toPlainString()}","apothemMm":"${calc.apothemMm.toPlainString()}","circumradiusMm":"${calc.circumradiusMm.toPlainString()}","interiorAngleDegrees":"${calc.interiorAngleDegrees.toPlainString()}","exteriorAngleDegrees":"${calc.exteriorAngleDegrees.toPlainString()}","centralAngleDegrees":"${calc.centralAngleDegrees.toPlainString()}","unit":"${calc.unit.name}","timestamp":${calc.timestamp},"isFavorite":${calc.isFavorite}}"""
        }
        return "[$items]"
    }

    fun fromJson(jsonStr: String): List<SavedCalculation> {
        val trimmed = jsonStr.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val content = trimmed.substring(1, trimmed.length - 1).trim()
        if (content.isEmpty()) return emptyList()

        val objects = splitJsonObjects(content)
        val result = mutableListOf<SavedCalculation>()

        for (objStr in objects) {
            val map = parseJsonObject(objStr)
            try {
                val calc = SavedCalculation(
                    id = map["id"] ?: System.currentTimeMillis().toString(),
                    projectName = map["projectName"] ?: "",
                    calculationMode = try {
                        PolygonCalculationMode.valueOf(map["calculationMode"] ?: PolygonCalculationMode.CALCULATE.name)
                    } catch (e: Exception) {
                        PolygonCalculationMode.CALCULATE
                    },
                    findSideMethod = try {
                        FindSideMethod.valueOf(map["findSideMethod"] ?: FindSideMethod.FROM_PERIMETER.name)
                    } catch (e: Exception) {
                        FindSideMethod.FROM_PERIMETER
                    },
                    sides = map["sides"]?.toIntOrNull() ?: 6,
                    sideLengthMm = BigDecimal(map["sideLengthMm"] ?: "100.0"),
                    perimeterMm = BigDecimal(map["perimeterMm"] ?: "600.0"),
                    areaMm2 = BigDecimal(map["areaMm2"] ?: "25980.76"),
                    apothemMm = BigDecimal(map["apothemMm"] ?: "86.6"),
                    circumradiusMm = BigDecimal(map["circumradiusMm"] ?: "100.0"),
                    interiorAngleDegrees = BigDecimal(map["interiorAngleDegrees"] ?: "120.0"),
                    exteriorAngleDegrees = BigDecimal(map["exteriorAngleDegrees"] ?: "60.0"),
                    centralAngleDegrees = BigDecimal(map["centralAngleDegrees"] ?: "60.0"),
                    unit = try {
                        MeasurementUnit.valueOf(map["unit"] ?: MeasurementUnit.CENTIMETER.name)
                    } catch (e: Exception) {
                        MeasurementUnit.CENTIMETER
                    },
                    timestamp = map["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
                    isFavorite = map["isFavorite"]?.toBoolean() ?: false
                )
                result.add(calc)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun splitJsonObjects(content: String): List<String> {
        val list = mutableListOf<String>()
        var depth = 0
        var start = -1
        var inString = false
        var isEscaped = false

        for (i in content.indices) {
            val c = content[i]
            if (isEscaped) {
                isEscaped = false
                continue
            }
            if (c == '\\') {
                isEscaped = true
                continue
            }
            if (c == '"') {
                inString = !inString
                continue
            }
            if (!inString) {
                if (c == '{') {
                    if (depth == 0) start = i
                    depth++
                } else if (c == '}') {
                    depth--
                    if (depth == 0 && start != -1) {
                        list.add(content.substring(start, i + 1))
                        start = -1
                    }
                }
            }
        }
        return list
    }

    private fun parseJsonObject(objStr: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val trimmed = objStr.trim().removeSurrounding("{", "}").trim()
        if (trimmed.isEmpty()) return map

        var inString = false
        var isEscaped = false
        var currentToken = StringBuilder()
        var currentKey: String? = null

        var i = 0
        while (i < trimmed.length) {
            val c = trimmed[i]
            if (isEscaped) {
                currentToken.append(c)
                isEscaped = false
                i++
                continue
            }
            if (c == '\\') {
                currentToken.append(c)
                isEscaped = true
                i++
                continue
            }
            if (c == '"') {
                inString = !inString
                currentToken.append(c)
                i++
                continue
            }
            if (!inString) {
                if (c == ':') {
                    val rawKey = currentToken.toString().trim()
                    currentKey = rawKey.removeSurrounding("\"")
                    currentToken = StringBuilder()
                    i++
                    continue
                } else if (c == ',') {
                    if (currentKey != null) {
                        val rawValue = currentToken.toString().trim()
                        val value = if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                            unescape(rawValue.removeSurrounding("\""))
                        } else {
                            rawValue
                        }
                        map[currentKey] = value
                        currentKey = null
                    }
                    currentToken = StringBuilder()
                    i++
                    continue
                }
            }
            currentToken.append(c)
            i++
        }

        if (currentKey != null) {
            val rawValue = currentToken.toString().trim()
            val value = if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                unescape(rawValue.removeSurrounding("\""))
            } else {
                rawValue
            }
            map[currentKey] = value
        }

        return map
    }
}

