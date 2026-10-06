package com.example.polygonpad.ui.viewmodel

import android.app.Application
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.polygonpad.data.math.BigDecimalMath
import com.example.polygonpad.data.math.PolygonFormatter
import com.example.polygonpad.data.math.PolygonPadCalculator
import com.example.polygonpad.data.math.UnitConverter
import com.example.polygonpad.data.model.AppSettings
import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.data.model.PolygonPreset
import com.example.polygonpad.data.model.PolygonResult
import com.example.polygonpad.data.model.SavedCalculation
import com.example.polygonpad.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

class PolygonPadViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    // Settings
    val settings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    // Input States
    private val _calculationMode = MutableStateFlow(PolygonCalculationMode.CALCULATE)
    val calculationMode: StateFlow<PolygonCalculationMode> = _calculationMode.asStateFlow()

    private val _findSideMethod = MutableStateFlow(FindSideMethod.FROM_PERIMETER)
    val findSideMethod: StateFlow<FindSideMethod> = _findSideMethod.asStateFlow()

    private val _projectName = MutableStateFlow("")
    val projectName: StateFlow<String> = _projectName.asStateFlow()

    private val _sidesText = MutableStateFlow("6")
    val sidesText: StateFlow<String> = _sidesText.asStateFlow()

    private val _sideLengthText = MutableStateFlow("10")
    val sideLengthText: StateFlow<String> = _sideLengthText.asStateFlow()

    private val _perimeterText = MutableStateFlow("60")
    val perimeterText: StateFlow<String> = _perimeterText.asStateFlow()

    private val _areaText = MutableStateFlow("259.81")
    val areaText: StateFlow<String> = _areaText.asStateFlow()

    private val _selectedUnit = MutableStateFlow(MeasurementUnit.CENTIMETER)
    val selectedUnit: StateFlow<MeasurementUnit> = _selectedUnit.asStateFlow()

    // Errors
    private val _sidesError = MutableStateFlow<String?>(null)
    val sidesError: StateFlow<String?> = _sidesError.asStateFlow()

    private val _dimensionError = MutableStateFlow<String?>(null)
    val dimensionError: StateFlow<String?> = _dimensionError.asStateFlow()

    // Calculated Result
    private val _currentResult = MutableStateFlow<PolygonResult?>(null)
    val currentResult: StateFlow<PolygonResult?> = _currentResult.asStateFlow()

    // UI events (snackbars)
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // History & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val historyList: StateFlow<List<SavedCalculation>> = combine(
        preferencesManager.historyFlow,
        _searchQuery
    ) { history, query ->
        if (query.isBlank()) {
            history
        } else {
            val q = query.trim().lowercase()
            history.filter { item ->
                item.projectName.lowercase().contains(q) ||
                        item.polygonName.lowercase().contains(q) ||
                        item.sides.toString().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe initial settings to seed default unit and calculation mode
        viewModelScope.launch {
            settings.collect { s ->
                // Only if untouched default, respect default settings
                if (_sidesText.value == "6" && _sideLengthText.value == "10" && _projectName.value.isEmpty()) {
                    _selectedUnit.value = s.defaultUnit
                    _calculationMode.value = s.defaultCalculationMode
                    _findSideMethod.value = s.defaultFindSideMethod
                }
            }
        }
        recalculate()
    }

    fun setCalculationMode(mode: PolygonCalculationMode, view: View? = null) {
        _calculationMode.value = mode
        triggerHaptic(view)
        recalculate()
    }

    fun setFindSideMethod(method: FindSideMethod, view: View? = null) {
        _findSideMethod.value = method
        triggerHaptic(view)
        recalculate()
    }

    fun setProjectName(name: String) {
        _projectName.value = name
    }

    fun setSides(sides: String) {
        _sidesText.value = sides
        recalculate()
    }

    fun selectPreset(preset: PolygonPreset, view: View? = null) {
        _sidesText.value = preset.sides.toString()
        triggerHaptic(view)
        recalculate()
    }

    fun setSideLength(length: String) {
        _sideLengthText.value = length
        recalculate()
    }

    fun selectSidePreset(lengthInCurrentUnit: String, view: View? = null) {
        _sideLengthText.value = lengthInCurrentUnit
        triggerHaptic(view)
        recalculate()
    }

    fun setPerimeter(perimeter: String) {
        _perimeterText.value = perimeter
        recalculate()
    }

    fun setArea(area: String) {
        _areaText.value = area
        recalculate()
    }

    fun setUnit(newUnit: MeasurementUnit) {
        val oldUnit = _selectedUnit.value
        if (oldUnit == newUnit) return

        // Changing units must preserve physical measurement!
        val currentSide = _sideLengthText.value.toBigDecimalOrNull()
        if (currentSide != null && currentSide > BigDecimal.ZERO) {
            val converted = UnitConverter.convertLinear(currentSide, oldUnit, newUnit)
            _sideLengthText.value = PolygonFormatter.formatNumber(converted, settings.value.decimalPlaces)
        }

        val currentPerim = _perimeterText.value.toBigDecimalOrNull()
        if (currentPerim != null && currentPerim > BigDecimal.ZERO) {
            val converted = UnitConverter.convertLinear(currentPerim, oldUnit, newUnit)
            _perimeterText.value = PolygonFormatter.formatNumber(converted, settings.value.decimalPlaces)
        }

        val currentArea = _areaText.value.toBigDecimalOrNull()
        if (currentArea != null && currentArea > BigDecimal.ZERO) {
            val converted = UnitConverter.convertArea(currentArea, oldUnit, newUnit)
            _areaText.value = PolygonFormatter.formatNumber(converted, settings.value.decimalPlaces)
        }

        _selectedUnit.value = newUnit
        recalculate()
    }

    fun recalculate() {
        _sidesError.value = null
        _dimensionError.value = null

        val sides = _sidesText.value.trim().toIntOrNull()
        if (sides == null) {
            _sidesError.value = "Enter a whole number of sides from 3 to 100."
            _currentResult.value = null
            return
        }

        if (sides !in PolygonPadCalculator.MIN_SIDES..PolygonPadCalculator.MAX_SIDES) {
            _sidesError.value = "Number of sides must be between 3 and 100."
            _currentResult.value = null
            return
        }

        when (_calculationMode.value) {
            PolygonCalculationMode.CALCULATE -> {
                val sideValue = _sideLengthText.value.trim().toBigDecimalOrNull()
                if (sideValue == null || sideValue <= BigDecimal.ZERO) {
                    _dimensionError.value = "Side length must be greater than zero."
                    _currentResult.value = null
                    return
                }
                try {
                    val sideLengthMm = UnitConverter.toMillimeters(sideValue, _selectedUnit.value)
                    val result = PolygonPadCalculator.calculatePolygon(sides, sideLengthMm)
                    _currentResult.value = result
                } catch (e: Exception) {
                    _dimensionError.value = e.message ?: "Invalid calculation parameters."
                    _currentResult.value = null
                }
            }
            PolygonCalculationMode.FIND_SIDE -> {
                when (_findSideMethod.value) {
                    FindSideMethod.FROM_PERIMETER -> {
                        val perimValue = _perimeterText.value.trim().toBigDecimalOrNull()
                        if (perimValue == null || perimValue <= BigDecimal.ZERO) {
                            _dimensionError.value = "Perimeter must be greater than zero."
                            _currentResult.value = null
                            return
                        }
                        try {
                            val perimMm = UnitConverter.toMillimeters(perimValue, _selectedUnit.value)
                            val sideMm = PolygonPadCalculator.calculateSideFromPerimeter(sides, perimMm)
                            val sideInUnit = UnitConverter.fromMillimeters(sideMm, _selectedUnit.value)
                            _sideLengthText.value = PolygonFormatter.formatNumber(sideInUnit, settings.value.decimalPlaces)
                            val result = PolygonPadCalculator.calculatePolygon(sides, sideMm)
                            _currentResult.value = result
                        } catch (e: Exception) {
                            _dimensionError.value = e.message ?: "Unable to calculate side from perimeter."
                            _currentResult.value = null
                        }
                    }
                    FindSideMethod.FROM_AREA -> {
                        val areaValue = _areaText.value.trim().toBigDecimalOrNull()
                        if (areaValue == null || areaValue <= BigDecimal.ZERO) {
                            _dimensionError.value = "Area must be greater than zero."
                            _currentResult.value = null
                            return
                        }
                        try {
                            val areaMm2 = UnitConverter.toSquareMillimeters(areaValue, _selectedUnit.value)
                            val sideMm = PolygonPadCalculator.calculateSideFromArea(sides, areaMm2)
                            val sideInUnit = UnitConverter.fromMillimeters(sideMm, _selectedUnit.value)
                            _sideLengthText.value = PolygonFormatter.formatNumber(sideInUnit, settings.value.decimalPlaces)
                            val result = PolygonPadCalculator.calculatePolygon(sides, sideMm)
                            _currentResult.value = result
                        } catch (e: Exception) {
                            _dimensionError.value = e.message ?: "Unable to calculate a valid side length."
                            _currentResult.value = null
                        }
                    }
                }
            }
        }
    }

    fun resetCalculator() {
        _projectName.value = ""
        _sidesText.value = "6"
        _sideLengthText.value = "10"
        _perimeterText.value = "60"
        _areaText.value = "259.81"
        _selectedUnit.value = settings.value.defaultUnit
        _calculationMode.value = settings.value.defaultCalculationMode
        _findSideMethod.value = settings.value.defaultFindSideMethod
        recalculate()
    }

    fun saveCalculation(view: View? = null) {
        val result = _currentResult.value ?: return
        triggerHaptic(view)
        viewModelScope.launch {
            val calc = SavedCalculation(
                id = UUID.randomUUID().toString(),
                projectName = _projectName.value.trim(),
                calculationMode = _calculationMode.value,
                findSideMethod = _findSideMethod.value,
                sides = result.sides,
                sideLengthMm = result.sideLengthMm,
                perimeterMm = result.perimeterMm,
                areaMm2 = result.areaMm2,
                apothemMm = result.apothemMm,
                circumradiusMm = result.circumradiusMm,
                interiorAngleDegrees = result.interiorAngleDegrees,
                exteriorAngleDegrees = result.exteriorAngleDegrees,
                centralAngleDegrees = result.centralAngleDegrees,
                unit = _selectedUnit.value,
                timestamp = System.currentTimeMillis()
            )
            preferencesManager.saveCalculation(calc)
            _uiEvent.emit("Calculation saved to history")
        }
    }

    fun restoreCalculation(calc: SavedCalculation) {
        _projectName.value = calc.projectName
        _calculationMode.value = calc.calculationMode
        _findSideMethod.value = calc.findSideMethod
        _sidesText.value = calc.sides.toString()
        _selectedUnit.value = calc.unit

        val sideInUnit = UnitConverter.fromMillimeters(calc.sideLengthMm, calc.unit)
        _sideLengthText.value = PolygonFormatter.formatNumber(sideInUnit, settings.value.decimalPlaces)

        val perimInUnit = UnitConverter.fromMillimeters(calc.perimeterMm, calc.unit)
        _perimeterText.value = PolygonFormatter.formatNumber(perimInUnit, settings.value.decimalPlaces)

        val areaInUnit = UnitConverter.fromSquareMillimeters(calc.areaMm2, calc.unit)
        _areaText.value = PolygonFormatter.formatNumber(areaInUnit, settings.value.decimalPlaces)

        recalculate()
    }

    fun toggleFavorite(id: String, view: View? = null) {
        triggerHaptic(view)
        viewModelScope.launch {
            preferencesManager.toggleFavorite(id)
        }
    }

    fun duplicateCalculation(calc: SavedCalculation, view: View? = null) {
        triggerHaptic(view)
        viewModelScope.launch {
            preferencesManager.duplicateCalculation(calc)
            _uiEvent.emit("Duplicated as 'Copy of ${calc.displayName}'")
        }
    }

    fun deleteCalculation(id: String, view: View? = null) {
        triggerHaptic(view)
        viewModelScope.launch {
            preferencesManager.deleteCalculation(id)
            _uiEvent.emit("Calculation removed")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            preferencesManager.clearHistory()
            _uiEvent.emit("History cleared")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            preferencesManager.updateSettings(newSettings)
            recalculate()
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            preferencesManager.resetSettings()
            _uiEvent.emit("Settings reset to defaults")
            recalculate()
        }
    }

    fun triggerHaptic(view: View?) {
        if (settings.value.hapticFeedback && view != null) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun getFormattedShareText(): String {
        val result = _currentResult.value ?: return ""
        return PolygonFormatter.formatShareText(
            sides = result.sides,
            sideLengthMm = result.sideLengthMm,
            areaMm2 = result.areaMm2,
            perimeterMm = result.perimeterMm,
            apothemMm = result.apothemMm,
            circumradiusMm = result.circumradiusMm,
            interiorAngle = result.interiorAngleDegrees,
            exteriorAngle = result.exteriorAngleDegrees,
            centralAngle = result.centralAngleDegrees,
            unit = _selectedUnit.value,
            projectName = _projectName.value,
            decimalPlaces = settings.value.decimalPlaces
        )
    }
}
