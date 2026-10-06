package com.example.polygonpad.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polygonpad.data.math.PolygonFormatter
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonResult
import com.example.polygonpad.ui.theme.DiagramApothem
import com.example.polygonpad.ui.theme.DiagramArc
import com.example.polygonpad.ui.theme.DiagramCenter
import com.example.polygonpad.ui.theme.DiagramHighlightSide
import com.example.polygonpad.ui.theme.DiagramPolygonFill
import com.example.polygonpad.ui.theme.DiagramPolygonStroke
import com.example.polygonpad.ui.theme.DiagramRadius
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PolygonDiagram(
    result: PolygonResult,
    unit: MeasurementUnit,
    decimalPlaces: Int = 2,
    showAngles: Boolean = true,
    showApothem: Boolean = true,
    showCircumradius: Boolean = true,
    modifier: Modifier = Modifier
) {
    val sides = result.sides
    val formattedSide = PolygonFormatter.formatDimension(result.sideLengthMm, unit, decimalPlaces)
    val formattedApothem = PolygonFormatter.formatDimension(result.apothemMm, unit, decimalPlaces)
    val formattedRadius = PolygonFormatter.formatDimension(result.circumradiusMm, unit, decimalPlaces)
    val formattedCentralAngle = PolygonFormatter.formatAngle(result.centralAngleDegrees, 0)

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    val density = LocalDensity.current
    val textSizePx = with(density) { 11.sp.toPx() }
    val labelTextSizePx = with(density) { 12.sp.toPx() }

    val accessibilityDesc = "Regular ${result.polygonName} with $sides sides, side length $formattedSide, apothem $formattedApothem, circumradius $formattedRadius"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(surfaceColor, RoundedCornerShape(16.dp))
            .border(1.dp, outlineColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .semantics { contentDescription = accessibilityDesc },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val centerOffset = Offset(centerX, centerY)

            // Radius in pixels for drawing
            val margin = if (sides <= 8) 48.dp.toPx() else 24.dp.toPx()
            val canvasRadius = (min(width, height) / 2f) - margin

            if (canvasRadius <= 0) return@Canvas

            // Compute regular polygon vertices
            val angleStep = (2 * PI) / sides
            val startAngle = if (sides == 4) -PI / 4 else -PI / 2

            val vertices = (0 until sides).map { i ->
                val angle = startAngle + i * angleStep
                val vx = centerX + (canvasRadius * cos(angle)).toFloat()
                val vy = centerY + (canvasRadius * sin(angle)).toFloat()
                Offset(vx, vy)
            }

            // 1. Draw polygon interior fill & outline
            val polygonPath = Path().apply {
                if (vertices.isNotEmpty()) {
                    moveTo(vertices[0].x, vertices[0].y)
                    for (i in 1 until vertices.size) {
                        lineTo(vertices[i].x, vertices[i].y)
                    }
                    close()
                }
            }

            drawPath(
                path = polygonPath,
                color = DiagramPolygonFill
            )

            drawPath(
                path = polygonPath,
                color = DiagramPolygonStroke.copy(alpha = 0.85f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Highlight one side (between vertex 0 and vertex 1)
            if (vertices.size >= 2) {
                val v0 = vertices[0]
                val v1 = vertices[1]
                drawLine(
                    color = DiagramHighlightSide,
                    start = v0,
                    end = v1,
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Side label for small/medium polygons
                if (sides <= 14) {
                    val sideMidX = (v0.x + v1.x) / 2f
                    val sideMidY = (v0.y + v1.y) / 2f
                    // Push label slightly outward from center
                    val normalX = (sideMidX - centerX) / canvasRadius
                    val normalY = (sideMidY - centerY) / canvasRadius
                    val labelOffsetDistance = 18.dp.toPx()
                    val labelX = sideMidX + normalX * labelOffsetDistance
                    val labelY = sideMidY + normalY * labelOffsetDistance

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = DiagramHighlightSide.toArgb()
                            textSize = labelTextSizePx
                            isAntiAlias = true
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText("Side = $formattedSide", labelX, labelY, paint)
                    }
                }
            }

            // 3. Apothem line (Center to midpoint of side 0-1)
            val v0 = vertices[0]
            val v1 = vertices[1]
            val apothemMidpoint = Offset((v0.x + v1.x) / 2f, (v0.y + v1.y) / 2f)

            if (showApothem) {
                drawLine(
                    color = DiagramApothem,
                    start = centerOffset,
                    end = apothemMidpoint,
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f),
                    cap = StrokeCap.Round
                )

                // Apothem label
                if (sides <= 14) {
                    val labelMidX = (centerX + apothemMidpoint.x) / 2f
                    val labelMidY = (centerY + apothemMidpoint.y) / 2f
                    val labelText = if (sides <= 8) "a = $formattedApothem" else "a"
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = DiagramApothem.toArgb()
                            textSize = textSizePx
                            isAntiAlias = true
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(labelText, labelMidX - 10.dp.toPx(), labelMidY, paint)
                    }
                }
            }

            // 4. Circumradius line (Center to vertex 0)
            if (showCircumradius) {
                drawLine(
                    color = DiagramRadius,
                    start = centerOffset,
                    end = v0,
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                    cap = StrokeCap.Round
                )

                // Radius label
                if (sides <= 14) {
                    val radiusMidX = (centerX + v0.x) / 2f
                    val radiusMidY = (centerY + v0.y) / 2f
                    val labelText = if (sides <= 8) "R = $formattedRadius" else "R"
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = DiagramRadius.toArgb()
                            textSize = textSizePx
                            isAntiAlias = true
                            textAlign = Paint.Align.RIGHT
                            isFakeBoldText = true
                        }
                        drawText(labelText, radiusMidX - 6.dp.toPx(), radiusMidY - 4.dp.toPx(), paint)
                    }
                }
            }

            // 5. Central Angle arc at center between angle of v0 and angle of v1
            if (showAngles && sides <= 12) {
                val arcRadius = 24.dp.toPx()
                val angle0Deg = Math.toDegrees(atan2((v0.y - centerY).toDouble(), (v0.x - centerX).toDouble())).toFloat()
                var sweep = (360f / sides)

                drawArc(
                    color = DiagramArc,
                    startAngle = angle0Deg,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(centerX - arcRadius, centerY - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = 1.8.dp.toPx())
                )

                if (sides <= 8) {
                    val midAngleRad = Math.toRadians((angle0Deg + sweep / 2).toDouble())
                    val arcLabelX = centerX + (arcRadius + 14.dp.toPx()) * cos(midAngleRad).toFloat()
                    val arcLabelY = centerY + (arcRadius + 14.dp.toPx()) * sin(midAngleRad).toFloat()

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = DiagramArc.toArgb()
                            textSize = 10.sp.toPx()
                            isAntiAlias = true
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(formattedCentralAngle, arcLabelX, arcLabelY + 4f, paint)
                    }
                }
            }

            // 6. Draw vertices points
            val vertexRadius = if (sides <= 12) 3.5.dp.toPx() else 2.dp.toPx()
            for (vertex in vertices) {
                drawCircle(
                    color = primaryColor,
                    radius = vertexRadius,
                    center = vertex
                )
            }

            // 7. Draw center point (subtle)
            drawCircle(
                color = DiagramCenter,
                radius = 3.dp.toPx(),
                center = centerOffset
            )
            drawCircle(
                color = surfaceColor,
                radius = 1.2.dp.toPx(),
                center = centerOffset
            )
        }
    }
}
