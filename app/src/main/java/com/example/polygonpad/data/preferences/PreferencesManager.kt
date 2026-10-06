package com.example.polygonpad.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.polygonpad.data.model.AppSettings
import com.example.polygonpad.data.model.AppTheme
import com.example.polygonpad.data.model.FindSideMethod
import com.example.polygonpad.data.model.MeasurementUnit
import com.example.polygonpad.data.model.PolygonCalculationMode
import com.example.polygonpad.data.model.SavedCalculation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "polygon_pad_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        const val MAX_HISTORY_ITEMS = 50

        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val KEY_DEFAULT_CALC_MODE = stringPreferencesKey("default_calc_mode")
        val KEY_DEFAULT_FIND_SIDE_METHOD = stringPreferencesKey("default_find_side_method")
        val KEY_DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val KEY_DECIMAL_PLACES = intPreferencesKey("decimal_places")
        val KEY_SHOW_DIAGRAM = booleanPreferencesKey("show_diagram")
        val KEY_SHOW_FORMULA = booleanPreferencesKey("show_formula")
        val KEY_SHOW_ANGLES = booleanPreferencesKey("show_angles")
        val KEY_SHOW_APOTHEM = booleanPreferencesKey("show_apothem")
        val KEY_SHOW_CIRCUMRADIUS = booleanPreferencesKey("show_circumradius")
        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val KEY_CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")

        val KEY_SAVED_CALCULATIONS = stringPreferencesKey("saved_calculations_json")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            theme = try {
                AppTheme.valueOf(preferences[KEY_THEME] ?: AppTheme.SYSTEM.name)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            },
            dynamicColor = preferences[KEY_DYNAMIC_COLOR] ?: true,
            defaultCalculationMode = try {
                PolygonCalculationMode.valueOf(preferences[KEY_DEFAULT_CALC_MODE] ?: PolygonCalculationMode.CALCULATE.name)
            } catch (e: Exception) {
                PolygonCalculationMode.CALCULATE
            },
            defaultFindSideMethod = try {
                FindSideMethod.valueOf(preferences[KEY_DEFAULT_FIND_SIDE_METHOD] ?: FindSideMethod.FROM_PERIMETER.name)
            } catch (e: Exception) {
                FindSideMethod.FROM_PERIMETER
            },
            defaultUnit = try {
                MeasurementUnit.valueOf(preferences[KEY_DEFAULT_UNIT] ?: MeasurementUnit.CENTIMETER.name)
            } catch (e: Exception) {
                MeasurementUnit.CENTIMETER
            },
            decimalPlaces = preferences[KEY_DECIMAL_PLACES] ?: 2,
            showDiagram = preferences[KEY_SHOW_DIAGRAM] ?: true,
            showFormula = preferences[KEY_SHOW_FORMULA] ?: true,
            showAngles = preferences[KEY_SHOW_ANGLES] ?: true,
            showApothem = preferences[KEY_SHOW_APOTHEM] ?: true,
            showCircumradius = preferences[KEY_SHOW_CIRCUMRADIUS] ?: true,
            hapticFeedback = preferences[KEY_HAPTIC_FEEDBACK] ?: true,
            confirmDelete = preferences[KEY_CONFIRM_DELETE] ?: true
        )
    }

    val historyFlow: Flow<List<SavedCalculation>> = context.dataStore.data.map { preferences ->
        val json = preferences[KEY_SAVED_CALCULATIONS] ?: ""
        val list = CalculationSerializer.fromJson(json)
        // Sort: Favorites first, then newest first
        list.sortedWith(
            compareByDescending<SavedCalculation> { it.isFavorite }
                .thenByDescending { it.timestamp }
        )
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = settings.theme.name
            prefs[KEY_DYNAMIC_COLOR] = settings.dynamicColor
            prefs[KEY_DEFAULT_CALC_MODE] = settings.defaultCalculationMode.name
            prefs[KEY_DEFAULT_FIND_SIDE_METHOD] = settings.defaultFindSideMethod.name
            prefs[KEY_DEFAULT_UNIT] = settings.defaultUnit.name
            prefs[KEY_DECIMAL_PLACES] = settings.decimalPlaces
            prefs[KEY_SHOW_DIAGRAM] = settings.showDiagram
            prefs[KEY_SHOW_FORMULA] = settings.showFormula
            prefs[KEY_SHOW_ANGLES] = settings.showAngles
            prefs[KEY_SHOW_APOTHEM] = settings.showApothem
            prefs[KEY_SHOW_CIRCUMRADIUS] = settings.showCircumradius
            prefs[KEY_HAPTIC_FEEDBACK] = settings.hapticFeedback
            prefs[KEY_CONFIRM_DELETE] = settings.confirmDelete
        }
    }

    suspend fun resetSettings() {
        val default = AppSettings()
        updateSettings(default)
    }

    suspend fun saveCalculation(calculation: SavedCalculation) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: ""
            val currentList = CalculationSerializer.fromJson(json).toMutableList()

            // Remove existing with same id if any
            currentList.removeAll { it.id == calculation.id }
            currentList.add(0, calculation)

            // Enforce maximum 50 saved calculations - remove oldest
            while (currentList.size > MAX_HISTORY_ITEMS) {
                currentList.removeAt(currentList.lastIndex)
            }

            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.toJson(currentList)
        }
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: ""
            val currentList = CalculationSerializer.fromJson(json).map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.toJson(currentList)
        }
    }

    suspend fun duplicateCalculation(calculation: SavedCalculation) {
        val originalName = calculation.projectName.ifBlank { "Untitled Polygon" }
        val copy = calculation.copy(
            id = UUID.randomUUID().toString(),
            projectName = "Copy of $originalName",
            timestamp = System.currentTimeMillis(),
            isFavorite = false
        )
        saveCalculation(copy)
    }

    suspend fun deleteCalculation(id: String) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: ""
            val currentList = CalculationSerializer.fromJson(json).filterNot { it.id == id }
            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.toJson(currentList)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs ->
            prefs[KEY_SAVED_CALCULATIONS] = "[]"
        }
    }
}
