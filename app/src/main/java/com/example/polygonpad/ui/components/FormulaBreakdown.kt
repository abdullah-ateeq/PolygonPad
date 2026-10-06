package com.example.polygonpad.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polygonpad.data.math.PolygonFormatter
import com.example.polygonpad.data.math.UnitConverter
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonResult

@Composable
fun FormulaBreakdown(
    result: PolygonResult,
    unit: MeasurementUnit,
    decimalPlaces: Int = 2,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val sides = result.sides
    val sideInUnit = UnitConverter.fromMillimeters(result.sideLengthMm, unit)
    val sStr = PolygonFormatter.formatNumber(sideInUnit, decimalPlaces)
    val pStr = PolygonFormatter.formatDimension(result.perimeterMm, unit, decimalPlaces)
    val aStr = PolygonFormatter.formatArea(result.areaMm2, unit, decimalPlaces)
    val apothemStr = PolygonFormatter.formatDimension(result.apothemMm, unit, decimalPlaces)
    val circumradiusStr = PolygonFormatter.formatDimension(result.circumradiusMm, unit, decimalPlaces)
    val interiorStr = PolygonFormatter.formatAngle(result.interiorAngleDegrees, decimalPlaces)
    val exteriorStr = PolygonFormatter.formatAngle(result.exteriorAngleDegrees, decimalPlaces)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "HOW IT WAS CALCULATED",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Step-by-step mathematical formulas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse formulas" else "Expand formulas",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormulaItem(
                    name = "Perimeter",
                    formula = "P = n × s",
                    calculation = "$sides × $sStr = $pStr"
                )

                FormulaItem(
                    name = "Interior Angle",
                    formula = "Interior = ((n − 2) × 180°) ÷ n",
                    calculation = "(($sides − 2) × 180°) ÷ $sides = $interiorStr"
                )

                FormulaItem(
                    name = "Exterior & Central Angle",
                    formula = "Exterior = Central = 360° ÷ n",
                    calculation = "360° ÷ $sides = $exteriorStr"
                )

                FormulaItem(
                    name = "Apothem",
                    formula = "a = s ÷ (2 × tan(π ÷ n))",
                    calculation = "$sStr ÷ (2 × tan(π ÷ $sides)) = $apothemStr"
                )

                FormulaItem(
                    name = "Circumradius",
                    formula = "R = s ÷ (2 × sin(π ÷ n))",
                    calculation = "$sStr ÷ (2 × sin(π ÷ $sides)) = $circumradiusStr"
                )

                FormulaItem(
                    name = "Area",
                    formula = "A = (n × s²) ÷ (4 × tan(π ÷ n))",
                    calculation = "($sides × ($sStr)²) ÷ (4 × tan(π ÷ $sides)) = $aStr"
                )
            }
        }
    }
}

@Composable
private fun FormulaItem(
    name: String,
    formula: String,
    calculation: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formula,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = calculation,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    }
}
