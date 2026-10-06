package com.example.polygonpad.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.polygonpad.data.model.COMMON_POLYGON_PRESETS
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonPreset

@Composable
fun PolygonPresetSelector(
    currentSides: Int,
    onSelectPreset: (PolygonPreset, android.view.View?) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "COMMON POLYGONS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            COMMON_POLYGON_PRESETS.forEach { preset ->
                val isSelected = preset.sides == currentSides
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPreset(preset, view) },
                    label = {
                        Text(
                            text = "${preset.name} (${preset.sides})",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun QuickSidePresets(
    unit: MeasurementUnit,
    onSelectPreset: (String, android.view.View?) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val presets = listOf("10", "25", "50", "100")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { value ->
            SuggestionChip(
                onClick = { onSelectPreset(value, view) },
                label = {
                    Text(
                        text = "$value ${unit.symbol}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}
