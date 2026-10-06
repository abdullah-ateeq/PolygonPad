package com.example.polygonpad.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polygonpad.ui.theme.DiagramApothem
import com.example.polygonpad.ui.theme.DiagramCenter
import com.example.polygonpad.ui.theme.DiagramHighlightSide
import com.example.polygonpad.ui.theme.DiagramPolygonFill
import com.example.polygonpad.ui.theme.DiagramPolygonStroke
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onNavigateToCalculator: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350)
        )
        delay(200)
        onNavigateToCalculator()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated technical polygon diagram
            Canvas(modifier = Modifier.size(160.dp)) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val radius = size.width * 0.42f
                val sides = 6
                val angleStep = (2 * PI) / sides
                val animVal = progress.value

                // Vertices for regular hexagon
                val vertices = (0 until sides).map { i ->
                    val angle = -PI / 2 + i * angleStep
                    Offset(
                        (centerX + radius * cos(angle)).toFloat(),
                        (centerY + radius * sin(angle)).toFloat()
                    )
                }

                // 1. Polygon outline
                if (vertices.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(vertices[0].x, vertices[0].y)
                        val totalSegments = vertices.size
                        val currentSegment = (animVal * totalSegments).toInt()
                        for (i in 1..currentSegment.coerceAtMost(vertices.size - 1)) {
                            lineTo(vertices[i].x, vertices[i].y)
                        }
                        if (animVal >= 0.95f) {
                            close()
                        }
                    }

                    if (animVal >= 0.85f) {
                        drawPath(path, DiagramPolygonFill)
                    }

                    drawPath(
                        path = path,
                        color = DiagramPolygonStroke,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 2. Center point
                if (animVal > 0.3f) {
                    drawCircle(
                        color = DiagramCenter,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                }

                // 3. Highlight side
                if (animVal > 0.6f && vertices.size >= 2) {
                    drawLine(
                        color = DiagramHighlightSide,
                        start = vertices[0],
                        end = vertices[1],
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // 4. Apothem line
                if (animVal > 0.7f && vertices.size >= 2) {
                    val midX = (vertices[0].x + vertices[1].x) / 2f
                    val midY = (vertices[0].y + vertices[1].y) / 2f
                    drawLine(
                        color = DiagramApothem,
                        start = Offset(centerX, centerY),
                        end = Offset(midX, midY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // 5. Vertices dots
                if (animVal > 0.5f) {
                    for (v in vertices) {
                        drawCircle(
                            color = DiagramPolygonStroke,
                            radius = 3.5.dp.toPx(),
                            center = v
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title & Subtitle
            Text(
                text = "POLYGONPAD",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Calculate regular polygon properties",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(titleAlpha.value)
            )
        }
    }
}
