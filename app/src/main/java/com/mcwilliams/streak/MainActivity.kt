package com.mcwilliams.streak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mcwilliams.streak.ui.dashboard.StravaDashboard
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.theme.StreakTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            StreakTheme {
                Scaffold(
                    content = { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = NavigationDestination.StravaDashboard.destination
                        ) {
                            composable(NavigationDestination.StravaDashboard.destination) {
                                val viewModel: StravaDashboardViewModel = hiltNavGraphViewModel()
                                StravaDashboard(
                                    viewModel = viewModel,
                                    paddingValues = paddingValues
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class NavigationDestination(
    val destination: String,
) {
    object StravaDashboard : NavigationDestination("stravaDashboard")
}