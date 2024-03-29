package com.mcwilliams.streak

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.MeasureType
import com.mcwilliams.streak.ui.dashboard.StravaDashboard
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.settings.StravaAuthWebView
import com.mcwilliams.streak.ui.settings.StreakSettingsView
import com.mcwilliams.streak.ui.spotifyjourney.SpotifyJourneyContent
import com.mcwilliams.streak.ui.theme.Material3Theme
import com.mcwilliams.streak.ui.theme.primaryColorShade1
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: StravaDashboardViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            Material3Theme(content = {
                val isLoggedIn by viewModel.isLoggedInStrava.observeAsState()
                var showLoginDialog by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableStateOf(0) }
                val selectedActivityType by viewModel.activityType.observeAsState(ActivityType.Run)
                val selectedUnitType by viewModel.unitType.observeAsState(UnitType.Imperial)
                val selectedMeasureType by viewModel.measureType.observeAsState(MeasureType.Absolute)

                isLoggedIn?.let {
                    if (it) {
                        Scaffold(
                            content = { paddingValues ->
                                val navBackStackEntry by navController.currentBackStackEntryAsState()

                                NavHost(
                                    navController,
                                    startDestination = NavigationDestination.StravaDashboard.destination
                                ) {
                                    composable(NavigationDestination.StravaDashboard.destination) {
                                        StravaDashboard(
                                            viewModel = viewModel,
                                            paddingValues = paddingValues
                                        )
                                    }
                                    composable(NavigationDestination.SpotifyJourney.destination){
                                        SpotifyJourneyContent()
                                    }
                                    composable(NavigationDestination.StreakSettings.destination) {
                                        StreakSettingsView(
                                            viewModel = viewModel,
                                            selectedActivityType = selectedActivityType,
                                            selectedUnitType = selectedUnitType,
                                            selectedMeasureType = selectedMeasureType
                                        )
                                    }
                                }
                            },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    val tintColor = MaterialTheme.colorScheme.onSurface

                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = {
                                            selectedTab = 0
                                            navController.navigate(NavigationDestination.StravaDashboard.destination) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Home,
                                                contentDescription = "",
                                                tint = if (selectedTab == 0) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                "Dashboard",
                                                color = if (selectedTab == 0) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = {
                                            selectedTab = 1
                                            navController.navigate(NavigationDestination.SpotifyJourney.destination) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = NavigationDestination.SpotifyJourney.resId!!),
                                                contentDescription = "",
                                                tint = if (selectedTab == 1) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                "Journey",
                                                color = if (selectedTab == 1) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = {
                                            selectedTab = 2
                                            navController.navigate(NavigationDestination.StreakSettings.destination) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "",
                                                tint = if (selectedTab == 2) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                "Settings",
                                                color = if (selectedTab == 2) tintColor else tintColor.copy(
                                                    .7f
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    } else {
                        Scaffold(
                            containerColor = primaryColorShade1,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            content = { paddingValues ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Welcome to Streak!",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "I have always wanted a way to analyze my athletic activity overtime, as well as track my progress",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "Streak provides snapshots of your Strava data organized by comparing your data week over week, month over month",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "Tap the \"Connect With Strava\" to login and get started",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                            .padding(paddingValues = paddingValues),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        val width = with(LocalDensity.current) { 772f.toDp() }
                                        val height = with(LocalDensity.current) { 192f.toDp() }
                                        Image(
                                            painter = painterResource(id = R.drawable.connect_with_strava),
                                            contentDescription = "Connect to strava",
                                            modifier = Modifier
                                                .size(width = width, height = height)
                                                .clickable {
                                                    showLoginDialog = !showLoginDialog
                                                })
                                    }
                                }

                                if (showLoginDialog) {
                                    val onFinish = { showLoginDialog = !showLoginDialog }
                                    Dialog(onDismissRequest = {
                                        showLoginDialog = !showLoginDialog
                                    }) {
                                        StravaAuthWebView(
                                            viewModel = viewModel,
                                            onFinish = onFinish
                                        )
                                    }
                                }

                            }
                        )
                    }
                }
            })
        }
    }
}

@Keep
sealed class NavigationDestination(
    val destination: String,
    val label: String? = null,
    val resId: Int? = null,
) {
    object StravaDashboard :
        NavigationDestination("stravaDashboard", "Dashboard", R.drawable.ic_dash)

    object SpotifyJourney : NavigationDestination("spotifyJourney", "Spotify Journey", R.drawable.ic_speed)

    object StreakSettings :
        NavigationDestination("streakSettings", "Settings", R.drawable.ic_settings)
}