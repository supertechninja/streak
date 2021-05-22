package com.mcwilliams.streak.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.BottomSheetValue.Collapsed
import androidx.compose.material.BottomSheetValue.Expanded
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.dashboard.widgets.MonthCompareWidget
import com.mcwilliams.streak.ui.dashboard.widgets.MonthWidget
import com.mcwilliams.streak.ui.dashboard.widgets.WeekCompareWidget
import com.mcwilliams.streak.ui.dashboard.widgets.WeekSummaryWidget
import com.mcwilliams.streak.ui.settings.StreakSettingsView
import com.mcwilliams.streak.ui.theme.StreakTheme
import com.mcwilliams.streak.ui.theme.primaryBlueShade2
import com.mcwilliams.streak.ui.theme.primaryColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun StravaDashboard(viewModel: StravaDashboardViewModel, paddingValues: PaddingValues) {
    val month = YearMonth.now()
    val firstDayOffset = month.atDay(1).dayOfWeek.ordinal
    val monthLength = month.lengthOfMonth()
    val priorMonthLength = month.minusMonths(1).lengthOfMonth()
    val lastDayCount = (monthLength + firstDayOffset) % 7
    val weekCount = (firstDayOffset + monthLength) / 7

    var fetchData by rememberSaveable { mutableStateOf(0) }

    StreakTheme {
        if (fetchData == 0) {
            viewModel.fetchData()
            fetchData = fetchData.inc()
        }
        val monthlyActivities by viewModel.currentMonthActivites.observeAsState()
        val previousMonthActivities by viewModel.previousMonthActivities.observeAsState()
        val previousPreviousMonthActivities by viewModel.previousPreviousMonthActivities.observeAsState()

        val last2MonthsActivities by viewModel.lastTwoMonthsActivities.observeAsState()

        val currentYearActivities by viewModel.currentYearActivites.observeAsState()
        val prevYearActivities by viewModel.prevYearActivites.observeAsState()
        val prevPrevYearActivities by viewModel.prevPrevYearActivites.observeAsState()

        val selectedActivityType by viewModel.activityType.observeAsState()

        val selectedUnitType by viewModel.unitType.observeAsState()

        var currentWeek: MutableList<Int> by rememberSaveable { mutableStateOf(mutableListOf()) }
        val updateCurrentWeek = { updatedWeek: MutableList<Int> ->
            currentWeek = updatedWeek
        }

        var currentMonthMetrics by remember {
            mutableStateOf(SummaryMetrics(0, 0f, 0f, 0))
        }
        val updateMonthlyMetrics = { summaryMetrics: SummaryMetrics ->
            currentMonthMetrics = summaryMetrics
        }

        val error by viewModel.error.observeAsState()

        val context = LocalContext.current
        val isRefreshing by viewModel.isRefreshing.observeAsState()

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

        BottomSheetScaffold(
            sheetContent = {
                StreakSettingsView(
                    viewModel = viewModel,
                    selectedActivityType = selectedActivityType,
                    selectedUnitType = selectedUnitType,
                    toggleBottomSheet = toggleBottomSheet
                )
            },
            sheetPeekHeight = 0.dp,
            scaffoldState = bottomSheetScaffoldState,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            sheetElevation = 20.dp,
            sheetBackgroundColor = primaryBlueShade2,
            topBar = {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFF01374D)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (title, action) = createRefs()
                        Text(
                            "Streak",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.constrainAs(title) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                        )

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    toggleBottomSheet()
                                }
                            },
                            modifier = Modifier.constrainAs(action) {
                                end.linkTo(parent.end, 16.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }

                }
            },
            content = {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing!!),
                    onRefresh = { viewModel.fetchData() },
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
                            .background(color = Color(0xFF01374D))
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

                        monthlyActivities?.let { monthlyWorkouts ->
                            Title(text = "Week Summary")

                            monthlyActivities?.let { monthlyWorkouts ->
                                WeekSummaryWidget(
                                    monthlyWorkouts = monthlyWorkouts,
                                    selectedActivityType = selectedActivityType,
                                    currentWeek = currentWeek,
                                    selectedUnitType = selectedUnitType
                                )
                            }

                            Title(text = "Month Summary")
                            MonthWidget(
                                monthlyWorkouts = monthlyWorkouts,
                                updateMonthlyMetrics,
                                selectedActivityType,
                                weekCount,
                                firstDayOffset,
                                lastDayCount,
                                priorMonthLength,
                                updateCurrentWeek,
                                selectedUnitType = selectedUnitType
                            )

                            Title(text = "Week vs Week")
                            last2MonthsActivities?.let { activitesList ->
                                WeekCompareWidget(
                                    activitesList = activitesList,
                                    selectedActivityType = selectedActivityType,
                                    selectedUnitType = selectedUnitType
                                )
                            }

                            if (previousMonthActivities != null && previousPreviousMonthActivities != null) {

                                var runCountPrev = 0
                                var prevDistance = 0f
                                var prevElevation = 0f
                                var prevTime = 0
                                previousMonthActivities?.forEach {
                                    if (selectedActivityType!!.name == ActivityType.All.name) {
                                        runCountPrev = runCountPrev.inc()
                                        prevDistance += it.distance
                                        prevElevation += it.total_elevation_gain
                                        prevTime += it.elapsed_time
                                    } else if (it.type == selectedActivityType!!.name) {
                                        runCountPrev = runCountPrev.inc()
                                        prevDistance += it.distance
                                        prevElevation += it.total_elevation_gain
                                        prevTime += it.elapsed_time
                                    }
                                }
                                val prevMetrics by remember {
                                    mutableStateOf(
                                        SummaryMetrics(
                                            runCountPrev,
                                            prevDistance,
                                            prevElevation,
                                            prevTime
                                        )
                                    )
                                }


                                var runCountPrevPrev = 0
                                var prevPrevDistance = 0f
                                var prevPrevElevation = 0f
                                var prevPrevTime = 0
                                previousPreviousMonthActivities?.forEach {
                                    if (selectedActivityType!!.name == ActivityType.All.name) {
                                        runCountPrevPrev = runCountPrevPrev.inc()
                                        prevPrevDistance += it.distance
                                        prevPrevElevation += it.total_elevation_gain
                                        prevPrevTime += it.elapsed_time
                                    } else if (it.type == selectedActivityType!!.name) {
                                        runCountPrevPrev = runCountPrevPrev.inc()
                                        prevPrevDistance += it.distance
                                        prevPrevElevation += it.total_elevation_gain
                                        prevPrevTime += it.elapsed_time
                                    }
                                }

                                val prevPrevMetrics by remember {
                                    mutableStateOf(
                                        SummaryMetrics(
                                            runCountPrevPrev,
                                            prevPrevDistance,
                                            prevPrevElevation,
                                            prevPrevTime
                                        )
                                    )
                                }

                                Title(text = "Month vs Month")
                                MonthCompareWidget(
                                    viewModel = viewModel,
                                    selectedActivityType = selectedActivityType,
                                    currentMonthMetrics = currentMonthMetrics,
                                    prevMetrics = prevMetrics,
                                    prevPrevMetrics = prevPrevMetrics,
                                    selectedUnitType = selectedUnitType
                                )

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
            })
    }
}

@Composable
fun ColumnScope.Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        fontSize = 18.sp,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
    )
}

enum class StatType { Distance, Time, Elevation, Count }

val monthWeekMap: MutableMap<Int, MutableList<Int>> = mutableMapOf()
val today = SimpleDateFormat("dd").format(Calendar.getInstance().time).toInt()

data class SummaryMetrics(
    val count: Int,
    val totalDistance: Float,
    val totalElevation: Float,
    val totalTime: Int
)

