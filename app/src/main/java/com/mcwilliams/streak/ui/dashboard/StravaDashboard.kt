package com.mcwilliams.streak.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.BottomSheetValue.Collapsed
import androidx.compose.material.BottomSheetValue.Expanded
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
import com.mcwilliams.streak.ui.settings.StreakSettingsView
import com.mcwilliams.streak.ui.theme.StreakTheme
import com.mcwilliams.streak.ui.theme.primaryBlueShade2
import com.mcwilliams.streak.ui.theme.primaryColor
import com.muddzdev.quickshot.QuickShot
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.dashboard.widgets.CompareWidget
import com.mcwilliams.streak.ui.dashboard.widgets.DashboardType
import com.mcwilliams.streak.ui.dashboard.widgets.WeeklySnapshot
import com.mcwilliams.streak.ui.utils.getDate


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalComposeApi
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

        var last2MonthsActivities: List<ActivitesItem> by remember { mutableStateOf(emptyList()) }

        val currentYearActivities by viewModel.currentYearActivites.observeAsState()
        val prevYearActivities by viewModel.prevYearActivites.observeAsState()
        val prevPrevYearActivities by viewModel.prevPrevYearActivites.observeAsState()

        val selectedActivityType by viewModel.activityType.observeAsState()

        val selectedUnitType by viewModel.unitType.observeAsState()

        val weeklyGoal by viewModel.weeklyGoal.observeAsState()

        var filterDashboards by remember { mutableStateOf(DashboardTypes.All) }

        var currentWeek: MutableList<Pair<Int, Int>> by rememberSaveable {
            mutableStateOf(
                mutableListOf()
            )
        }
        val updateCurrentWeek = { updatedWeek: MutableList<Pair<Int, Int>> ->
            currentWeek = updatedWeek
        }

        var currentMonthMetrics by rememberSaveable {
            mutableStateOf(SummaryMetrics(0, 0f, 0f, 0))
        }
        val updateMonthlyMetrics = { summaryMetrics: SummaryMetrics ->
            currentMonthMetrics = summaryMetrics
        }

        val error by viewModel.error.observeAsState()

        val context = LocalContext.current
        val isRefreshing by viewModel.isRefreshing.observeAsState()

        val today by viewModel.today.observeAsState()

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                        .background(color = Color(0xFF01374D))
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing!!),
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
                                .background(color = Color(0xFF01374D))
                                .animateContentSize()
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

                            val filterScrollState = rememberScrollState(0)
                            val coroutineScope = rememberCoroutineScope()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                                    .horizontalScroll(filterScrollState),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DashboardTypes.values().forEach {
                                    val borderSize by animateDpAsState(targetValue = if (it == filterDashboards) 2.dp else 0.dp)
                                    val borderColor by animateColorAsState(targetValue = if (it == filterDashboards) Color.White else primaryBlueShade2)
                                    val textSize by animateFloatAsState(targetValue = if (it == filterDashboards) 18.sp.value else 16.sp.value)

                                    val padding by animateDpAsState(targetValue = if (it == filterDashboards) 8.dp else 16.dp)

                                    Surface(
                                        shape = CircleShape,
                                        color = primaryBlueShade2,
                                        elevation = 4.dp,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable {
                                                filterDashboards = it
                                            },
                                        border = BorderStroke(
                                            borderSize,
                                            borderColor
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                it.name,
                                                style = MaterialTheme.typography.subtitle1,
                                                color = MaterialTheme.colors.onSurface,
                                                modifier = Modifier
                                                    .padding(
                                                        vertical = 4.dp,
                                                    )
                                                    .padding(start = 16.dp, end = padding),
                                                fontSize = TextUnit(textSize, TextUnitType.Sp)
                                            )

                                            AnimatedVisibility(visible = it == filterDashboards) {
                                                Image(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                                                    modifier = Modifier.padding(end = 8.dp)
                                                )
                                            }
                                        }
                                    }

                                    if(it == DashboardTypes.Year){
                                        coroutineScope.launch {
                                            filterScrollState.animateScrollTo(filterScrollState.maxValue)
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            filterScrollState.animateScrollTo(0)
                                        }
                                    }
                                }
                            }

                            var weeklySummaryMetrics: SummaryMetrics = SummaryMetrics()

                            monthlyActivities?.let { monthlyWorkouts ->
                                previousMonthActivities?.let {
                                    last2MonthsActivities = monthlyWorkouts.plus(it)

                                    val dayOfWeekWithDistance: MutableMap<Int, Int> = mutableMapOf()
                                    var totalDistance = 0f
                                    var totalElevation = 0f
                                    var totalTime = 0
                                    var count = 0
                                    var average = 0f

                                    monthlyWorkouts.forEach { activitesItem ->
                                        val date = activitesItem.start_date_local.getDate()

                                        currentWeek.forEach {
                                            if (it.second == date.dayOfMonth && it.first == date.monthValue) {
                                                if (selectedActivityType!!.name == ActivityType.All.name) {
                                                    totalElevation += activitesItem.total_elevation_gain

                                                    totalTime += activitesItem.elapsed_time

                                                    totalDistance += activitesItem.distance

                                                    if (dayOfWeekWithDistance.containsKey(date.dayOfMonth)) {
                                                        val newDistance =
                                                            dayOfWeekWithDistance.get(date.dayOfMonth)!! + activitesItem.distance.toInt()
                                                        dayOfWeekWithDistance.replace(
                                                            date.dayOfMonth,
                                                            newDistance
                                                        )
                                                    } else {
                                                        dayOfWeekWithDistance.put(
                                                            date.dayOfMonth,
                                                            activitesItem.distance.toInt()
                                                        )
                                                    }

                                                    count = count.inc()
                                                } else if (activitesItem.type == selectedActivityType!!.name) {
                                                    totalElevation += activitesItem.total_elevation_gain

                                                    totalTime += activitesItem.elapsed_time

                                                    totalDistance += activitesItem.distance

                                                    if (dayOfWeekWithDistance.containsKey(date.dayOfMonth)) {
                                                        val newDistance =
                                                            dayOfWeekWithDistance.get(date.dayOfMonth)!! + activitesItem.distance.toInt()
                                                        dayOfWeekWithDistance.replace(
                                                            date.dayOfMonth,
                                                            newDistance
                                                        )
                                                    } else {
                                                        dayOfWeekWithDistance.put(
                                                            date.dayOfMonth,
                                                            activitesItem.distance.toInt()
                                                        )
                                                    }

                                                    average += activitesItem.average_speed.toFloat()

                                                    count = count.inc()
                                                }
                                            }
                                        }

                                        weeklySummaryMetrics = SummaryMetrics(
                                            count = count,
                                            totalElevation = totalElevation,
                                            totalDistance = totalDistance,
                                            totalTime = totalTime,
                                            averageSpeed = ((average / count) * 2.237)
                                        )

                                    }

                                    if (filterDashboards == DashboardTypes.All || filterDashboards == DashboardTypes.Week) {
                                        StreakDashboardWidget(
                                            content = {
                                                WeeklySnapshot(
                                                    weeklySummaryMetrics = weeklySummaryMetrics,
                                                    dayOfWeekWithDistance = dayOfWeekWithDistance,
                                                    selectedActivityType = selectedActivityType,
                                                    currentWeek = currentWeek,
                                                    selectedUnitType = selectedUnitType,
//                                                    weeklyGoal = weeklyGoal
                                                )
                                            },
                                            widgetName = "Week Snapshot"
                                        )

                                        StreakDashboardWidget(
                                            content = {
                                                WeekSummaryWidget(
                                                    weeklySummaryMetrics = weeklySummaryMetrics,
                                                    dayOfWeekWithDistance = dayOfWeekWithDistance,
                                                    selectedActivityType = selectedActivityType,
                                                    currentWeek = currentWeek,
                                                    selectedUnitType = selectedUnitType,
                                                )
                                            },
                                            widgetName = "Week Summary"
                                        )
                                    }

                                }

                                if (filterDashboards == DashboardTypes.All || filterDashboards == DashboardTypes.Month) {
                                    StreakDashboardWidget(
                                        content = {
                                            MonthWidget(
                                                monthlyWorkouts = monthlyWorkouts,
                                                updateMonthlyMetrics,
                                                selectedActivityType,
                                                weekCount,
                                                firstDayOffset,
                                                lastDayCount,
                                                priorMonthLength,
                                                updateCurrentWeek,
                                                selectedUnitType = selectedUnitType,
                                                today = today
                                            )
                                        }, widgetName = "Month Summary"
                                    )
                                }

                                if (filterDashboards == DashboardTypes.All || filterDashboards == DashboardTypes.Week) {
                                    StreakDashboardWidget(
                                        content = {
                                            WeekCompareWidget(
                                                activitesList = last2MonthsActivities,
                                                selectedActivityType = selectedActivityType,
                                                selectedUnitType = selectedUnitType,
                                                today = today!!
                                            )
                                        }, widgetName = "Week vs Week"
                                    )
                                }

                                if (previousMonthActivities != null && previousPreviousMonthActivities != null) {
                                    val prevMetrics by remember {
                                        mutableStateOf(
                                            getStats(
                                                previousMonthActivities!!,
                                                selectedActivity = selectedActivityType!!
                                            )
                                        )
                                    }

                                    val prevPrevMetrics by remember {
                                        mutableStateOf(
                                            getStats(
                                                previousPreviousMonthActivities!!,
                                                selectedActivity = selectedActivityType!!
                                            )
                                        )
                                    }

                                    if (filterDashboards == DashboardTypes.All || filterDashboards == DashboardTypes.Month) {
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
                                                    prevMetrics = prevMetrics,
                                                    prevPrevMetrics = prevPrevMetrics,
                                                    selectedUnitType = selectedUnitType
                                                )
                                            }, widgetName = "Month vs Month"
                                        )
                                    }

                                    prevYearActivities?.let { lastYearActivities ->
                                        val lastYearSummaryMetrics = getStats(
                                            lastYearActivities,
                                            selectedActivity = selectedActivityType!!
                                        )

                                        var currentYearSummaryMetrics = SummaryMetrics()
                                        var lastLastYearSummaryMetrics = SummaryMetrics()

                                        prevPrevYearActivities?.let {
                                            lastLastYearSummaryMetrics = getStats(
                                                it,
                                                selectedActivity = selectedActivityType!!
                                            )
                                        }

                                        currentYearActivities?.let {
                                            currentYearSummaryMetrics = getStats(
                                                it,
                                                selectedActivity = selectedActivityType!!
                                            )
                                        }

                                        if (filterDashboards == DashboardTypes.All || filterDashboards == DashboardTypes.Year) {
                                            StreakDashboardWidget(
                                                content = {
                                                    CompareWidget(
                                                        dashboardType = DashboardType.Year,
                                                        selectedActivityType = selectedActivityType,
                                                        columnTitles = arrayOf(
                                                            "2021",
                                                            "2020",
                                                            "2019"
                                                        ),
                                                        currentMonthMetrics = currentYearSummaryMetrics,
                                                        prevMetrics = lastYearSummaryMetrics,
                                                        prevPrevMetrics = lastLastYearSummaryMetrics,
                                                        selectedUnitType = selectedUnitType
                                                    )
                                                }, widgetName = "Year vs Year"
                                            )
                                        }

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
                }
            }
        )
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
                    style = MaterialTheme.typography.h6,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 16.dp)
                )

                val fileName = widgetName.replace(" ", "-")
                IconButton(
                    onClick = {
                        share(composeView, fileName, context = context)
                    },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Share"
                    )
                }
            }
        }

        return@AndroidView androidView
    })
}

enum class StatType { Distance, Time, Elevation, Count }

enum class DashboardTypes { All, Week, Month, Year }

val monthWeekMap: MutableMap<Int, MutableList<Pair<Int, Int>>> = mutableMapOf()

data class SummaryMetrics(
    val count: Int = 0,
    val totalDistance: Float = 0f,
    val totalElevation: Float = 0f,
    val totalTime: Int = 0,
    val averageSpeed: Double = 0.0
)

private fun share(view: ComposeView, name: String, context: Context) {
    val fileName = "$name-${LocalDate.now()}-${LocalTime.now().hour}-${LocalTime.now().minute}"
    QuickShot.of(view).setResultListener(HandleSavedImage(context, fileName))
        .setFilename(fileName)
        .setPath("Streak")
        .toPNG()
//        .toJPG()
        .save();
}

class HandleSavedImage(val context: Context, val fileName: String) : QuickShot.QuickShotListener {
    override fun onQuickShotSuccess(path: String?) {
        Toast.makeText(context, "Dashboard Saved", Toast.LENGTH_SHORT).show()
    }

    override fun onQuickShotFailed(path: String?) {
        Toast.makeText(context, "Error Saving", Toast.LENGTH_SHORT).show()
    }

}

fun getStats(
    activitiesToFilter: List<ActivitesItem>,
    selectedActivity: ActivityType
): SummaryMetrics {
    val filteredActivities =
        if (selectedActivity == ActivityType.All) activitiesToFilter
        else activitiesToFilter.filter { it.type == selectedActivity.name }

    var count = 0
    var distance = 0f
    var elevation = 0f
    var time = 0

    filteredActivities.forEach {
        count = count.inc()
        distance += it.distance
        elevation += it.total_elevation_gain
        time += it.elapsed_time
    }

    return SummaryMetrics(
        count = count,
        totalDistance = distance,
        totalTime = time,
        totalElevation = elevation
    )
}
