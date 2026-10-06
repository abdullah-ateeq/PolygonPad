package com.example.polygonpad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.polygonpad.ui.navigation.PolygonNavGraph
import com.example.polygonpad.ui.theme.PolygonPadTheme
import com.example.polygonpad.ui.viewmodel.PolygonPadViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PolygonPadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            PolygonPadTheme(
                appTheme = settings.theme,
                dynamicColor = settings.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PolygonNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}