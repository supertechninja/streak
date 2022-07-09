package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.BottomSheetValue.Collapsed
import androidx.compose.material.BottomSheetValue.Expanded
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.dashboard.widgets.MonthWidget
import com.mcwilliams.streak.ui.dashboard.widgets.WeekCompareWidget
import com.mcwilliams.streak.ui.dashboard.widgets.WeekSummaryWidget
import com.mcwilliams.streak.ui.theme.primaryColor
import kotlinx.coroutines.launch
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.widgets.CompareWidget
import com.mcwilliams.streak.ui.dashboard.widgets.DashboardType


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun StravaDashboard(viewModel: StravaDashboardViewModel, paddingValues: PaddingValues) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchData()
        fetchData = fetchData.inc()
    }
    val monthlyActivities by viewModel.currentMonthActivites.observeAsState(emptyList())
    val previousMonthActivities by viewModel.previousMonthActivities.observeAsState(emptyList())
    val previousPreviousMonthActivities by viewModel.previousPreviousMonthActivities.observeAsState(
        emptyList()
    )

    var last2MonthsActivities: List<ActivitiesItem> by remember { mutableStateOf(emptyList()) }

    val currentYearActivities by viewModel.currentYearActivites.observeAsState()
    val prevYearActivities by viewModel.prevYearActivites.observeAsState()
    val prevPrevYearActivities by viewModel.prevPrevYearActivites.observeAsState()

    val selectedActivityType by viewModel.activityType.observeAsState(ActivityType.Run)

    val selectedUnitType by viewModel.unitType.observeAsState(UnitType.Imperial)

    val today by viewModel.today.observeAsState()

    val monthWeekMap by viewModel.monthWeekMap.observeAsState(mutableMapOf())
    val currentWeek by viewModel.currentWeek.observeAsState(mutableListOf())

    var currentMonthMetrics by remember {
        mutableStateOf(SummaryMetrics(0, 0f, 0f, 0))
    }
    val updateMonthlyMetrics = { summaryMetrics: SummaryMetrics ->
        currentMonthMetrics = summaryMetrics
    }

    var currentYearSummaryMetrics by remember { mutableStateOf(SummaryMetrics()) }

    val error by viewModel.error.observeAsState()

    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)

    var refreshState = rememberSwipeRefreshState(isRefreshing)
    refreshState.isRefreshing = isRefreshing
    Log.d("REFRESH", "StravaDashboard: $isRefreshing")

    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(initialValue = Collapsed))

    val coroutineScope = rememberCoroutineScope()
    val toggleBottomSheet = {
        coroutineScope.launch {
            when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
                Collapsed -> {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
                Expanded -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
        }
    }

    val saveWeeklyDistance = { weeklyDistance: String, weeklyElevation: String ->
        viewModel.saveWeeklyStats(weeklyDistance, weeklyElevation)
    }

    Scaffold(topBar = {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (title, action) = createRefs()

                Text(
                    "Streak",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.secondary)
            ) {
                SwipeRefresh(
                    state = refreshState,
                    onRefresh = {
                        viewModel.fetchData()
                    },
                    indicator = { s, trigger ->
                        SwipeRefreshIndicator(
                            s,
                            trigger,
                            contentColor = primaryColor,
                            backgroundColor = Color.White
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues = paddingValues)
                            .verticalScroll(rememberScrollState())
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {

                        error?.let {
                            if (it.isNotEmpty()) {
                                Snackbar(action = {
                                    Text(text = "Refresh")
                                }) {
                                    Text(text = it)
                                }
                            }
                        }


                        last2MonthsActivities = monthlyActivities.plus(previousMonthActivities)

                        StreakDashboardWidget(
                            content = {
                                WeekSummaryWidget(
                                    monthlyWorkouts = last2MonthsActivities,
                                    selectedActivityType = selectedActivityType,
                                    currentWeek = currentWeek,
                                    selectedUnitType = selectedUnitType,
                                    today = today!!,
                                    isLoading = last2MonthsActivities.isEmpty(),
                                    saveWeeklyStats = saveWeeklyDistance,
                                )
                            },
                            widgetName = "Week Summary"
                        )

                        StreakDashboardWidget(
                            content = {
                                MonthWidget(
                                    monthlyWorkouts = monthlyActivities,
                                    updateMonthlyMetrics = updateMonthlyMetrics,
                                    selectedActivityType = selectedActivityType,
                                    selectedUnitType = selectedUnitType,
                                    monthWeekMap = monthWeekMap,
                                    today = today,
                                    isLoading = monthlyActivities.isEmpty()
                                )
                            }, widgetName = "Month Summary"
                        )

                        StreakDashboardWidget(
                            content = {
                                WeekCompareWidget(
                                    activitesList = last2MonthsActivities,
                                    selectedActivityType = selectedActivityType,
                                    selectedUnitType = selectedUnitType,
                                    today = today!!,
                                    monthWeekMap = monthWeekMap,
                                    isLoading = last2MonthsActivities.isEmpty()
                                )
                            }, widgetName = "Week vs Week"
                        )

                        StreakDashboardWidget(
                            content = {
                                CompareWidget(
                                    dashboardType = DashboardType.Month,
                                    selectedActivityType = selectedActivityType,
                                    currentMonthMetrics = currentMonthMetrics,
                                    columnTitles = arrayOf(
                                        viewModel.currentMonth,
                                        viewModel.previousMonth,
                                        viewModel.previousPreviousMonth
                                    ),
                                    prevMetrics = previousMonthActivities.getStats(
                                        selectedActivityType
                                    ),
                                    prevPrevMetrics = previousPreviousMonthActivities.getStats(
                                        selectedActivityType
                                    ),
                                    selectedUnitType = selectedUnitType
                                )
                            }, widgetName = "Month vs Month"
                        )

                        prevYearActivities?.let { lastYearActivities ->
                            val lastYearSummaryMetrics =
                                lastYearActivities.getStats(
                                    selectedActivityType
                                )

                            var lastLastYearSummaryMetrics = SummaryMetrics()

                            prevPrevYearActivities?.let {
                                lastLastYearSummaryMetrics =
                                    it.getStats(
                                        selectedActivityType
                                    )
                            }

                            currentYearActivities?.let {
                                currentYearSummaryMetrics =
                                    it.getStats(
                                        selectedActivityType
                                    )

                                viewModel.currentYearSummaryMetrics = currentYearSummaryMetrics
                            }

                            StreakDashboardWidget(
                                content = {
                                    CompareWidget(
                                        dashboardType = DashboardType.Year,
                                        selectedActivityType = selectedActivityType,
                                        columnTitles = arrayOf("2022", "2021", "2020"),
                                        currentMonthMetrics = currentYearSummaryMetrics,
                                        prevMetrics = lastYearSummaryMetrics,
                                        prevPrevMetrics = lastLastYearSummaryMetrics,
                                        selectedUnitType = selectedUnitType
                                    )
                                }, widgetName = "Year vs Year"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.powerd_by_strava_logo),
                                contentDescription = "Powered By Strava",
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ColumnScope.Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
    )
}


@Composable
fun StreakDashboardWidget(content: @Composable () -> Unit, widgetName: String) {
    AndroidView(factory = { context ->
        val androidView =
            LayoutInflater.from(context)
                .inflate(R.layout.compose_view, null)


        val composeView =
            androidView.findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            content()
        }

        val titleComposeView =
            androidView.findViewById<ComposeView>(R.id.title_compose_view)
        titleComposeView.setContent {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widgetName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 16.dp)
                )
            }
        }

        return@AndroidView androidView
    })
}

enum class StatType { Distance, Time, Elevation, Count, Pace }

data class SummaryMetrics(
    val count: Int = 0,
    val totalDistance: Float = 0f,
    val totalElevation: Float = 0f,
    val totalTime: Int = 0
)

fun List<ActivitiesItem>.getStats(selectedActivity: ActivityType): SummaryMetrics {
    val filteredActivities =
        if (selectedActivity == ActivityType.All) this
        else this.filter { it.type == selectedActivity.name }

    var count = 0
    var distance = 0f
    var elevation = 0f
    var time = 0

    filteredActivities.forEach {
        count = count.inc()
        distance += it.distance
        elevation += it.total_elevation_gain
        time += it.moving_time
    }

    return SummaryMetrics(
        count = count,
        totalDistance = distance,
        totalTime = time,
        totalElevation = elevation
    )
}
