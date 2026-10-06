package com.example.polygonpad.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.polygonpad.data.model.AppTheme
import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.ui.viewmodel.PolygonPadViewModel

@Composable
fun SettingsScreen(
    viewModel: PolygonPadViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Preferences, display units, and calculation defaults",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Appearance Section
        SettingsCard(title = "APPEARANCE") {
            // Theme Selector
            Text(
                text = "Theme",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    FilterChip(
                        selected = settings.theme == theme,
                        onClick = { viewModel.updateSettings(settings.copy(theme = theme)) },
                        label = { Text(theme.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dynamic Color Toggle
            SettingsToggleRow(
                title = "Dynamic Color",
                subtitle = "Match system wallpaper colors on Android 12+",
                checked = settings.dynamicColor,
                onCheckedChange = { viewModel.updateSettings(settings.copy(dynamicColor = it)) }
            )
        }

        // Calculation Defaults Section
        SettingsCard(title = "CALCULATION DEFAULTS") {
            // Default Calculation Mode
            Text(
                text = "Default Mode",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PolygonCalculationMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.defaultCalculationMode == mode,
                        onClick = { viewModel.updateSettings(settings.copy(defaultCalculationMode = mode)) },
                        label = { Text(mode.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Default Find Side Method
            Text(
                text = "Default Find Side Method",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FindSideMethod.entries.forEach { method ->
                    FilterChip(
                        selected = settings.defaultFindSideMethod == method,
                        onClick = { viewModel.updateSettings(settings.copy(defaultFindSideMethod = method)) },
                        label = { Text(method.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Default Unit
            Text(
                text = "Default Unit",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MeasurementUnit.entries.forEach { unit ->
                    FilterChip(
                        selected = settings.defaultUnit == unit,
                        onClick = { viewModel.updateSettings(settings.copy(defaultUnit = unit)) },
                        label = { Text(unit.symbol) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Decimal Places
            Text(
                text = "Decimal Places: ${settings.decimalPlaces}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0, 1, 2, 3, 4).forEach { places ->
                    FilterChip(
                        selected = settings.decimalPlaces == places,
                        onClick = { viewModel.updateSettings(settings.copy(decimalPlaces = places)) },
                        label = { Text(places.toString()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Display Options Section
        SettingsCard(title = "DISPLAY & FEEDBACK") {
            SettingsToggleRow(
                title = "Show Diagram",
                subtitle = "Display mathematical polygon visualization",
                checked = settings.showDiagram,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showDiagram = it)) }
            )

            SettingsToggleRow(
                title = "Show Formula Breakdown",
                subtitle = "Collapsible mathematical calculation steps",
                checked = settings.showFormula,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showFormula = it)) }
            )

            SettingsToggleRow(
                title = "Show Angles",
                subtitle = "Display interior, exterior, and central angles",
                checked = settings.showAngles,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showAngles = it)) }
            )

            SettingsToggleRow(
                title = "Show Apothem",
                subtitle = "Display center to side midpoint dimension",
                checked = settings.showApothem,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showApothem = it)) }
            )

            SettingsToggleRow(
                title = "Show Circumradius",
                subtitle = "Display center to vertex dimension",
                checked = settings.showCircumradius,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showCircumradius = it)) }
            )

            SettingsToggleRow(
                title = "Haptic Feedback",
                subtitle = "Vibrate on preset taps and actions",
                checked = settings.hapticFeedback,
                onCheckedChange = { viewModel.updateSettings(settings.copy(hapticFeedback = it)) }
            )

            SettingsToggleRow(
                title = "Confirm Delete",
                subtitle = "Ask before deleting saved calculations",
                checked = settings.confirmDelete,
                onCheckedChange = { viewModel.updateSettings(settings.copy(confirmDelete = it)) }
            )
        }

        // Actions Section
        SettingsCard(title = "DATA & RESET") {
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset All Settings to Defaults")
            }

            OutlinedButton(
                onClick = { showClearHistoryDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear All Saved Calculations")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Reset Settings Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all preferences to default values? Your calculation history will be kept.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetSettings()
                    showResetDialog = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Delete all saved calculations? This action cannot be reversed.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearHistoryDialog = false
                }) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
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
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp
        )
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
