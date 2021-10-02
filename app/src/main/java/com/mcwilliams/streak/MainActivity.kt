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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mcwilliams.streak.ui.bottomnavigation.BottomNavEffect
import com.mcwilliams.streak.ui.dashboard.ActivityType
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieAnimationSpec
//import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.mcwilliams.streak.ui.dashboard.StravaDashboard
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.goals.GoalsContent
import com.mcwilliams.streak.ui.settings.StravaAuthWebView
import com.mcwilliams.streak.ui.settings.StreakSettingsView
import com.mcwilliams.streak.ui.theme.StreakTheme
import com.mcwilliams.streak.ui.theme.primaryColorShade1
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : ComponentActivity() {
    private val viewModel: StravaDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val items = listOf(
                NavigationDestination.StravaDashboard,
                NavigationDestination.StreakSettings,
            )

            StreakTheme {
                val isLoggedIn by viewModel.isLoggedIn.observeAsState()
                var showLoginDialog by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableStateOf(1) }
                var updateSelectedTab = { tab: Int ->
                    selectedTab = tab
                }

                val selectedActivityType by viewModel.activityType.observeAsState(ActivityType.Run)
                val selectedUnitType by viewModel.unitType.observeAsState(UnitType.Imperial)

                isLoggedIn?.let {
                    if (it) {
                        Scaffold(
                            content = { paddingValues ->
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

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
                                    composable(NavigationDestination.Goals.destination) {
                                        GoalsContent(
                                            viewModel = viewModel,
                                        )
                                    }
                                    composable(NavigationDestination.StreakSettings.destination) {
                                        StreakSettingsView(
                                            viewModel = viewModel,
                                            selectedActivityType = selectedActivityType,
                                            selectedUnitType = selectedUnitType
                                        )
                                    }
                                }
                            },
                            bottomBar = {
                                Surface(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {
                                    BottomNavEffect(
                                        selectedTab = selectedTab,
                                        updateSelectedTab = updateSelectedTab,
                                        navController = navController
                                    )
                                }
                            }
                        )
                    } else {
                        Scaffold(
                            backgroundColor = primaryColorShade1,
                            contentColor = MaterialTheme.colors.onSurface,
                            content = { paddingValues ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Welcome to Streak!",
                                        style = MaterialTheme.typography.h4,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "I have always wanted a way to analyze my athletic activity overtime, as well as track my progress",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "Streak provides snapshots of your Strava data organized by comparing your data week over week, month over month",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Text(
                                        text = "Tap the \"Connect With Strava\" to login and get started",
                                        style = MaterialTheme.typography.subtitle1,
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
            }
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

    object Goals : NavigationDestination("goals", "Goals", R.drawable.ic_clock_time)

    object StreakSettings :
        NavigationDestination("streakSettings", "Settings", R.drawable.ic_settings)
}