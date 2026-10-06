package com.example.polygonpad.ui.screens.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.data.model.PolygonResult
import com.example.polygonpad.ui.components.FormulaBreakdown
import com.example.polygonpad.ui.components.PolygonDiagram
import com.example.polygonpad.ui.components.PolygonPresetSelector
import com.example.polygonpad.ui.components.PrimaryResultCards
import com.example.polygonpad.ui.components.QuickSidePresets
import com.example.polygonpad.ui.components.SecondaryResultSection
import com.example.polygonpad.ui.viewmodel.PolygonPadViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: PolygonPadViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    val calculationMode by viewModel.calculationMode.collectAsState()
    val findSideMethod by viewModel.findSideMethod.collectAsState()
    val projectName by viewModel.projectName.collectAsState()
    val sidesText by viewModel.sidesText.collectAsState()
    val sideLengthText by viewModel.sideLengthText.collectAsState()
    val perimeterText by viewModel.perimeterText.collectAsState()
    val areaText by viewModel.areaText.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val sidesError by viewModel.sidesError.collectAsState()
    val dimensionError by viewModel.dimensionError.collectAsState()
    val currentResult by viewModel.currentResult.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var unitMenuExpanded by remember { mutableStateOf(false) }

    // Listen to ViewModel UI events (Snackbar notifications)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Two-column layout for tablets / landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Inputs
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CalculatorHeader()

                    ModeSelector(
                        mode = calculationMode,
                        onModeSelected = { viewModel.setCalculationMode(it, view) },
                        findSideMethod = findSideMethod,
                        onFindSideMethodSelected = { viewModel.setFindSideMethod(it, view) }
                    )

                    PolygonPresetSelector(
                        currentSides = sidesText.toIntOrNull() ?: 0,
                        onSelectPreset = { preset, v -> viewModel.selectPreset(preset, v) }
                    )

                    InputFields(
                        projectName = projectName,
                        onProjectNameChange = { viewModel.setProjectName(it) },
                        sidesText = sidesText,
                        onSidesChange = { viewModel.setSides(it) },
                        sidesError = sidesError,
                        mode = calculationMode,
                        findSideMethod = findSideMethod,
                        sideLengthText = sideLengthText,
                        onSideLengthChange = { viewModel.setSideLength(it) },
                        perimeterText = perimeterText,
                        onPerimeterChange = { viewModel.setPerimeter(it) },
                        areaText = areaText,
                        onAreaChange = { viewModel.setArea(it) },
                        selectedUnit = selectedUnit,
                        unitMenuExpanded = unitMenuExpanded,
                        onUnitMenuExpandedChange = { unitMenuExpanded = it },
                        onUnitSelected = { viewModel.setUnit(it) },
                        dimensionError = dimensionError,
                        onQuickSidePreset = { preset, v -> viewModel.selectSidePreset(preset, v) }
                    )

                    ActionButtons(
                        onReset = { viewModel.resetCalculator() },
                        onSave = { viewModel.saveCalculation(view) },
                        onCopy = {
                            val shareText = viewModel.getFormattedShareText()
                            if (shareText.isNotEmpty()) {
                                copyToClipboard(context, shareText)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Result copied")
                                }
                            }
                        },
                        onShare = {
                            val shareText = viewModel.getFormattedShareText()
                            if (shareText.isNotEmpty()) {
                                shareResult(context, shareText)
                            }
                        },
                        hasResult = currentResult != null
                    )

                    DisclaimerSection()
                }

                // Right Column: Diagram and Results
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentResult != null) {
                        ResultDisplaySection(
                            result = currentResult!!,
                            selectedUnit = selectedUnit,
                            settings = settings
                        )
                    } else {
                        IncompleteInputCard(sidesError = sidesError, dimensionError = dimensionError)
                    }
                }
            }
        } else {
            // Single-column layout for phones portrait
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CalculatorHeader()

                ModeSelector(
                    mode = calculationMode,
                    onModeSelected = { viewModel.setCalculationMode(it, view) },
                    findSideMethod = findSideMethod,
                    onFindSideMethodSelected = { viewModel.setFindSideMethod(it, view) }
                )

                PolygonPresetSelector(
                    currentSides = sidesText.toIntOrNull() ?: 0,
                    onSelectPreset = { preset, v -> viewModel.selectPreset(preset, v) }
                )

                InputFields(
                    projectName = projectName,
                    onProjectNameChange = { viewModel.setProjectName(it) },
                    sidesText = sidesText,
                    onSidesChange = { viewModel.setSides(it) },
                    sidesError = sidesError,
                    mode = calculationMode,
                    findSideMethod = findSideMethod,
                    sideLengthText = sideLengthText,
                    onSideLengthChange = { viewModel.setSideLength(it) },
                    perimeterText = perimeterText,
                    onPerimeterChange = { viewModel.setPerimeter(it) },
                    areaText = areaText,
                    onAreaChange = { viewModel.setArea(it) },
                    selectedUnit = selectedUnit,
                    unitMenuExpanded = unitMenuExpanded,
                    onUnitMenuExpandedChange = { unitMenuExpanded = it },
                    onUnitSelected = { viewModel.setUnit(it) },
                    dimensionError = dimensionError,
                    onQuickSidePreset = { preset, v -> viewModel.selectSidePreset(preset, v) }
                )

                if (currentResult != null) {
                    ResultDisplaySection(
                        result = currentResult!!,
                        selectedUnit = selectedUnit,
                        settings = settings
                    )
                } else {
                    IncompleteInputCard(sidesError = sidesError, dimensionError = dimensionError)
                }

                ActionButtons(
                    onReset = { viewModel.resetCalculator() },
                    onSave = { viewModel.saveCalculation(view) },
                    onCopy = {
                        val shareText = viewModel.getFormattedShareText()
                        if (shareText.isNotEmpty()) {
                            copyToClipboard(context, shareText)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Result copied")
                            }
                        }
                    },
                    onShare = {
                        val shareText = viewModel.getFormattedShareText()
                        if (shareText.isNotEmpty()) {
                            shareResult(context, shareText)
                        }
                    },
                    hasResult = currentResult != null
                )

                DisclaimerSection()
            }
        }
    }
}

@Composable
private fun CalculatorHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "POLYGONPAD",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Regular Polygon Rule Badge
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Regular polygon",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Text(
            text = "Calculate regular polygon properties.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModeSelector(
    mode: PolygonCalculationMode,
    onModeSelected: (PolygonCalculationMode) -> Unit,
    findSideMethod: FindSideMethod,
    onFindSideMethodSelected: (FindSideMethod) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PolygonCalculationMode.entries.forEach { m ->
                FilterChip(
                    selected = mode == m,
                    onClick = { onModeSelected(m) },
                    label = {
                        Text(
                            text = m.label.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Secondary selector when FIND SIDE is selected
        if (mode == PolygonCalculationMode.FIND_SIDE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FindSideMethod.entries.forEach { method ->
                    FilterChip(
                        selected = findSideMethod == method,
                        onClick = { onFindSideMethodSelected(method) },
                        label = {
                            Text(
                                text = method.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InputFields(
    projectName: String,
    onProjectNameChange: (String) -> Unit,
    sidesText: String,
    onSidesChange: (String) -> Unit,
    sidesError: String?,
    mode: PolygonCalculationMode,
    findSideMethod: FindSideMethod,
    sideLengthText: String,
    onSideLengthChange: (String) -> Unit,
    perimeterText: String,
    onPerimeterChange: (String) -> Unit,
    areaText: String,
    onAreaChange: (String) -> Unit,
    selectedUnit: MeasurementUnit,
    unitMenuExpanded: Boolean,
    onUnitMenuExpandedChange: (Boolean) -> Unit,
    onUnitSelected: (MeasurementUnit) -> Unit,
    dimensionError: String?,
    onQuickSidePreset: (String, android.view.View?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Optional Project Name
        OutlinedTextField(
            value = projectName,
            onValueChange = onProjectNameChange,
            label = { Text("Project Name (Optional)") },
            placeholder = { Text("e.g. Hexagonal Table, Garden Border") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Number of sides input
        OutlinedTextField(
            value = sidesText,
            onValueChange = onSidesChange,
            label = { Text("Number of Sides (3 to 100)") },
            isError = sidesError != null,
            supportingText = {
                if (sidesError != null) {
                    Text(text = sidesError, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Whole number between 3 and 100")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Dimension input with Unit dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            when (mode) {
                PolygonCalculationMode.CALCULATE -> {
                    OutlinedTextField(
                        value = sideLengthText,
                        onValueChange = onSideLengthChange,
                        label = { Text("Side Length (${selectedUnit.symbol})") },
                        isError = dimensionError != null,
                        supportingText = {
                            if (dimensionError != null) {
                                Text(text = dimensionError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                PolygonCalculationMode.FIND_SIDE -> {
                    when (findSideMethod) {
                        FindSideMethod.FROM_PERIMETER -> {
                            OutlinedTextField(
                                value = perimeterText,
                                onValueChange = onPerimeterChange,
                                label = { Text("Perimeter (${selectedUnit.symbol})") },
                                isError = dimensionError != null,
                                supportingText = {
                                    if (dimensionError != null) {
                                        Text(text = dimensionError, color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        FindSideMethod.FROM_AREA -> {
                            OutlinedTextField(
                                value = areaText,
                                onValueChange = onAreaChange,
                                label = { Text("Area (${selectedUnit.areaSymbol})") },
                                isError = dimensionError != null,
                                supportingText = {
                                    if (dimensionError != null) {
                                        Text(text = dimensionError, color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Unit Dropdown selector
            Box(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(
                    onClick = { onUnitMenuExpandedChange(true) }
                ) {
                    Text(
                        text = if (mode == PolygonCalculationMode.FIND_SIDE && findSideMethod == FindSideMethod.FROM_AREA)
                            selectedUnit.areaSymbol
                        else selectedUnit.symbol,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select unit")
                }

                DropdownMenu(
                    expanded = unitMenuExpanded,
                    onDismissRequest = { onUnitMenuExpandedChange(false) }
                ) {
                    MeasurementUnit.entries.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text("${unit.displayName} (${unit.symbol})") },
                            onClick = {
                                onUnitSelected(unit)
                                onUnitMenuExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }

        // Quick Side Presets chips when in CALCULATE mode
        if (mode == PolygonCalculationMode.CALCULATE) {
            QuickSidePresets(
                unit = selectedUnit,
                onSelectPreset = onQuickSidePreset
            )
        }
    }
}

@Composable
private fun ResultDisplaySection(
    result: PolygonResult,
    selectedUnit: MeasurementUnit,
    settings: com.example.polygonpad.data.model.AppSettings
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formal Polygon Label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = PolygonResult.getFormalPolygonLabel(result.sides),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${result.sides} SIDES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Primary Results (Area & Perimeter)
        PrimaryResultCards(
            result = result,
            unit = selectedUnit,
            decimalPlaces = settings.decimalPlaces
        )

        // 2. Polygon Diagram
        if (settings.showDiagram) {
            PolygonDiagram(
                result = result,
                unit = selectedUnit,
                decimalPlaces = settings.decimalPlaces,
                showAngles = settings.showAngles,
                showApothem = settings.showApothem,
                showCircumradius = settings.showCircumradius
            )
        }

        // 3. Secondary Results
        SecondaryResultSection(
            result = result,
            unit = selectedUnit,
            decimalPlaces = settings.decimalPlaces,
            showAngles = settings.showAngles,
            showApothem = settings.showApothem,
            showCircumradius = settings.showCircumradius
        )

        // 4. Dynamic Formula Breakdown
        if (settings.showFormula) {
            FormulaBreakdown(
                result = result,
                unit = selectedUnit,
                decimalPlaces = settings.decimalPlaces
            )
        }
    }
}

@Composable
private fun IncompleteInputCard(
    sidesError: String?,
    dimensionError: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Enter the required values.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val detail = sidesError ?: dimensionError
            if (detail != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onReset: () -> Unit,
    onSave: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    hasResult: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = hasResult,
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("SAVE")
            }

            Button(
                onClick = onCopy,
                enabled = hasResult,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("COPY")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                enabled = hasResult,
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("SHARE")
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("RESET")
            }
        }
    }
}

@Composable
private fun DisclaimerSection() {
    Text(
        text = "PolygonPad performs basic geometric calculations for regular polygons only. Verify measurements, tolerances, material specifications, and project requirements before fabrication or construction.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("PolygonPad Result", text)
    clipboard.setPrimaryClip(clip)
}

private fun shareResult(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share PolygonPad Result")
    context.startActivity(shareIntent)
}
