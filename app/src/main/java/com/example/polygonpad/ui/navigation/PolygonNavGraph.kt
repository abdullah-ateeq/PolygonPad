package com.example.polygonpad.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.polygonpad.ui.screens.about.AboutScreen
import com.example.polygonpad.ui.screens.calculator.CalculatorScreen
import com.example.polygonpad.ui.screens.history.HistoryScreen
import com.example.polygonpad.ui.screens.settings.SettingsScreen
import com.example.polygonpad.ui.screens.splash.SplashScreen
import com.example.polygonpad.ui.viewmodel.PolygonPadViewModel

@Composable
fun PolygonNavGraph(
    viewModel: PolygonPadViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Splash.route

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(imageVector = icon, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToCalculator = {
                        navController.navigate(Screen.Calculator.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onOpenCalculation = {
                        navController.navigate(Screen.Calculator.route) {
                            popUpTo(Screen.Calculator.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
