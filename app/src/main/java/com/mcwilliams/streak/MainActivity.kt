package com.mcwilliams.streak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.mcwilliams.streak.ui.dashboard.StravaDashboard
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.settings.StravaAuthWebView
import com.mcwilliams.streak.ui.settings.StreakSettingsView
import com.mcwilliams.streak.ui.theme.StreakTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@AndroidEntryPoint
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

                if (isLoggedIn!!) {

                    Scaffold(
                        content = { paddingValues ->
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
                                composable(NavigationDestination.StreakSettings.destination) {
                                    StreakSettingsView(
                                        viewModel = viewModel,
                                        paddingValues = paddingValues
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            BottomNavigation {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute =
                                    navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                                items.forEach { screen ->
                                    BottomNavigationItem(
                                        icon = {
                                            val animationSpec =
                                                remember { LottieAnimationSpec.RawRes(screen.resId!!) }

                                            LottieAnimation(
                                                animationSpec,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text(screen.label!!) },
                                        selected = currentRoute == screen.destination,
                                        onClick = {
                                            navController.navigate(screen.destination) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo = navController.graph.startDestination
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Scaffold(content = { paddingValues ->
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
                                text = "I have always wanted a way to analyze my athletic activity overtime.",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                text = "Streak provides snapshots of your Strava data organized by comparing your data week over week, month over month, and year over year",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                text = "Tap the \" Connect With Strava \" to login and get started",
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
                            Dialog(onDismissRequest = { showLoginDialog = !showLoginDialog }) {
                                StravaAuthWebView(viewModel = viewModel, onFinish = onFinish)
                            }
                        }
                    })
                }
            }
        }
    }
}

sealed class NavigationDestination(
    val destination: String,
    val label: String? = null,
    val resId: Int? = null,
) {
    object StravaDashboard :
        NavigationDestination("stravaDashboard", "Dashboard", R.raw.ic_bar_chart)

    object StreakSettings : NavigationDestination("streakSettings", "Settings", R.raw.ic_settings)
}