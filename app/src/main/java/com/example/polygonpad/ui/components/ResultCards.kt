package com.example.polygonpad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polygonpad.data.math.PolygonFormatter
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonResult

@Composable
fun PrimaryResultCards(
    result: PolygonResult,
    unit: MeasurementUnit,
    decimalPlaces: Int,
    modifier: Modifier = Modifier
) {
    val formattedArea = PolygonFormatter.formatArea(result.areaMm2, unit, decimalPlaces)
    val formattedPerimeter = PolygonFormatter.formatDimension(result.perimeterMm, unit, decimalPlaces)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Area Card (Visually Dominant)
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
                .semantics {
                    contentDescription = "Area $formattedArea"
                }
        ) {
            Text(
                text = "AREA",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedArea,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Perimeter Card (Visually Dominant)
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
                .semantics {
                    contentDescription = "Perimeter $formattedPerimeter"
                }
        ) {
            Text(
                text = "PERIMETER",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedPerimeter,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SecondaryResultSection(
    result: PolygonResult,
    unit: MeasurementUnit,
    decimalPlaces: Int,
    showAngles: Boolean,
    showApothem: Boolean,
    showCircumradius: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedSide = PolygonFormatter.formatDimension(result.sideLengthMm, unit, decimalPlaces)
    val formattedApothem = PolygonFormatter.formatDimension(result.apothemMm, unit, decimalPlaces)
    val formattedRadius = PolygonFormatter.formatDimension(result.circumradiusMm, unit, decimalPlaces)
    val formattedInterior = PolygonFormatter.formatAngle(result.interiorAngleDegrees, decimalPlaces)
    val formattedExterior = PolygonFormatter.formatAngle(result.exteriorAngleDegrees, decimalPlaces)
    val formattedCentral = PolygonFormatter.formatAngle(result.centralAngleDegrees, decimalPlaces)

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "GEOMETRIC PROPERTIES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.2.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PropertyRowItem(
                label = "NUMBER OF SIDES",
                value = result.sides.toString(),
                accessibilityLabel = "${result.sides} sides",
                modifier = Modifier.weight(1f)
            )
            PropertyRowItem(
                label = "SIDE LENGTH",
                value = formattedSide,
                accessibilityLabel = "Side length $formattedSide",
                modifier = Modifier.weight(1f)
            )
        }

        if (showApothem || showCircumradius) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showApothem) {
                    PropertyRowItem(
                        label = "APOTHEM",
                        value = formattedApothem,
                        accessibilityLabel = "Apothem $formattedApothem",
                        modifier = Modifier.weight(1f)
                    )
                }
                if (showCircumradius) {
                    PropertyRowItem(
                        label = "CIRCUMRADIUS",
                        value = formattedRadius,
                        accessibilityLabel = "Circumradius $formattedRadius",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showAngles) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PropertyRowItem(
                    label = "INTERIOR ANGLE",
                    value = formattedInterior,
                    accessibilityLabel = "Interior angle $formattedInterior",
                    modifier = Modifier.weight(1f)
                )
                PropertyRowItem(
                    label = "EXTERIOR ANGLE",
                    value = formattedExterior,
                    accessibilityLabel = "Exterior angle $formattedExterior",
                    modifier = Modifier.weight(1f)
                )
            }

            PropertyRowItem(
                label = "CENTRAL ANGLE",
                value = formattedCentral,
                accessibilityLabel = "Central angle $formattedCentral",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PropertyRowItem(
    label: String,
    value: String,
    accessibilityLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .semantics { contentDescription = accessibilityLabel }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
