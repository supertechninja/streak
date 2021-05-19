package com.mcwilliams.streak.ui.dashboard

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.settings.StravaAuthWebView
import com.mcwilliams.streak.ui.theme.StreakTheme
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.*
import kotlin.math.roundToInt

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

        val currentYearActivities by viewModel.currentYearActivites.observeAsState()
        val prevYearActivities by viewModel.prevYearActivites.observeAsState()
        val prevPrevYearActivities by viewModel.prevPrevYearActivites.observeAsState()

        val selectedActivityType by viewModel.activityType.observeAsState()
        val activityLabel = selectedActivityType?.name

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Streak Dashboard",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                )

                IconButton(onClick = { fetchData = 0 }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFF036e9a)
            ) {
                BoxWithConstraints(
                    modifier = Modifier.padding(
                        vertical = 12.dp,
                        horizontal = 10.dp
                    )
                ) {
                    val width = this.maxWidth / 2

                    val rightWidth = (this.maxWidth.times(.6f))
                    val leftWidth = this.maxWidth - rightWidth

                    monthlyActivities?.let { monthlyWorkouts ->

                        var listOfDaysLoggedActivity = mutableListOf<Int>()
                        var totalDistance = 0f
                        var totalElevation = 0f
                        var totalTime = 0
                        var count = 0

                        monthlyWorkouts.forEach {
                            if (it.type == selectedActivityType!!.name) {
                                val date = it.start_date_local.getDate()
                                listOfDaysLoggedActivity.add(date.dayOfMonth)

                                totalElevation += it.total_elevation_gain

                                totalTime += it.elapsed_time

                                totalDistance += it.distance

                                count = count.inc()
                            }
                        }

                        listOfDaysLoggedActivity =
                            listOfDaysLoggedActivity.distinct().toMutableList()

                        currentMonthMetrics = MonthMetrics(
                            count = count,
                            totalDistance = totalDistance,
                            totalElevation = totalElevation,
                            totalTime = totalTime
                        )

                        Row() {
                            Column(
                                modifier = Modifier.width(width = leftWidth),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row() {
                                    Text(
                                        activityLabel!!,
                                        color = Color(0xFFFFA500),
                                    )
                                    Text(
                                        "  |  ${
                                            month.month.name.toLowerCase(Locale.getDefault())
                                                .capitalize(Locale.getDefault())
                                        }",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .padding(vertical = 4.dp)
                                        .height(1.dp), color = MaterialTheme.colors.onSurface
                                )
                                DashboardStat(
                                    image = R.drawable.ic_ruler,
                                    stat = "${totalDistance.div(1609).roundToInt()} mi"
                                )

                                DashboardStat(
                                    image = R.drawable.ic_clock_time,
                                    stat = totalTime.getTimeStringHoursAndMinutes()
                                )

                                DashboardStat(
                                    image = R.drawable.ic_up_right,
                                    stat = "${totalElevation.getElevationString()}"
                                )

                                DashboardStat(image = R.drawable.ic_hashtag, stat = "$count")
                            }
                            Column(modifier = Modifier.width(width = rightWidth)) {
                                monthlyActivities?.let {
                                    for (i in 0..weekCount) {
                                        CalendarView(
                                            startDayOffSet = firstDayOffset,
                                            endDayCount = lastDayCount,
                                            monthWeekNumber = i,
                                            priorMonthLength = priorMonthLength,
                                            weekCount = weekCount,
                                            width = rightWidth,
                                            daysActivitiesLogged = listOfDaysLoggedActivity
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFF036e9a)
            ) {

                val dayOfWeekWithDistance: MutableMap<Int, Int> = mutableMapOf()
                BoxWithConstraints(
                    modifier = Modifier.padding(
                        vertical = 12.dp,
                        horizontal = 10.dp
                    )
                ) {
                    val width = this.maxWidth / 2

                    monthlyActivities?.let { monthlyWorkouts ->

                        var totalDistance = 0f
                        var totalElevation = 0f
                        var totalTime = 0
                        var count = 0

                        monthlyWorkouts.forEach { activitesItem ->
                            val date = activitesItem.start_date_local.getDate()

                            if (currentWeek.contains(date.dayOfMonth)) {
                                if (activitesItem.type == selectedActivityType!!.name) {
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
                                }
                            }
                        }

                        Row() {
                            Column(
                                modifier = Modifier.width(width = width),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row() {
                                    Text(
                                        text = activityLabel!!,
                                        color = Color(0xFFFFA500),
                                    )
                                    Text(
                                        "  |  May ${
                                            currentWeek[0]
                                        }-${
                                            currentWeek.last()
                                        }",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .padding(vertical = 4.dp)
                                        .height(1.dp), color = MaterialTheme.colors.onSurface
                                )
                                DashboardStat(
                                    image = R.drawable.ic_ruler,
                                    stat = "${totalDistance.div(1609).roundToInt()} mi"
                                )

                                DashboardStat(
                                    image = R.drawable.ic_clock_time,
                                    stat = totalTime.getTimeStringHoursAndMinutes()
                                )

                                DashboardStat(
                                    image = R.drawable.ic_up_right,
                                    stat = "${totalElevation.getElevationString()}"
                                )

                                DashboardStat(image = R.drawable.ic_hashtag, stat = "$count")
                            }
                            Column(
                                modifier = Modifier
                                    .width(width = width)
                                    .height(120.dp)
                            ) {
                                monthlyActivities?.let {

                                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                                        val (progress, days) = createRefs()

                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            modifier = Modifier.constrainAs(progress) {
                                                bottom.linkTo(days.top)
                                                start.linkTo(parent.start)
                                                end.linkTo(parent.end)
                                            }) {
                                            currentWeek.forEach { dateInWeek ->
                                                Column(
                                                    modifier = Modifier.width(width / 7),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    dayOfWeekWithDistance.forEach {
                                                        if (it.key == dateInWeek) {
                                                            Log.d(
                                                                "TAG",
                                                                "StravaDashboard: ${it.value}"
                                                            )
                                                            val progressHeight =
                                                                when (it.value.div(1609)) {
                                                                    in 1..2 -> {
                                                                        20.dp
                                                                    }
                                                                    in 2..5 -> {
                                                                        50.dp
                                                                    }
                                                                    in 5..8 -> {
                                                                        65.dp
                                                                    }
                                                                    in 8..100 -> {
                                                                        90.dp
                                                                    }
                                                                    else -> {
                                                                        0.dp
                                                                    }
                                                                }
                                                            if (it.value > 0) {
                                                                Divider(
                                                                    color = Color(0xFFFFA500),
                                                                    modifier = Modifier
                                                                        .height(progressHeight)
                                                                        .width(15.dp)
                                                                        .clip(
                                                                            RoundedCornerShape(
                                                                                50
                                                                            )
                                                                        )
                                                                        .padding(horizontal = 4.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            modifier = Modifier.constrainAs(days) {
                                                bottom.linkTo(parent.bottom)
                                                start.linkTo(parent.start)
                                                end.linkTo(parent.end)
                                            }) {
                                            DayOfWeek.values().forEach {
                                                Column(
                                                    modifier = Modifier.width(width / 7),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(it.name.substring(0, 1))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (previousMonthActivities != null && previousPreviousMonthActivities != null) {

                var runCountPrev = 0
                var prevDistance = 0f
                var prevElevation = 0f
                var prevTime = 0
                previousMonthActivities?.forEach {
                    if (it.type == selectedActivityType!!.name) {
                        runCountPrev = runCountPrev.inc()
                        prevDistance += it.distance
                        prevElevation += it.total_elevation_gain
                        prevTime += it.elapsed_time
                    }
                }

                var runCountPrevPrev = 0
                var prevPrevDistance = 0f
                var prevPrevElevation = 0f
                var prevPrevTime = 0
                previousPreviousMonthActivities?.forEach {
                    if (it.type == selectedActivityType!!.name) {
                        runCountPrevPrev = runCountPrevPrev.inc()
                        prevPrevDistance += it.distance
                        prevPrevElevation += it.total_elevation_gain
                        prevPrevTime += it.elapsed_time
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF036e9a)
                ) {

                    BoxWithConstraints(
                        modifier = Modifier.padding(
                            vertical = 12.dp,
                            horizontal = 10.dp
                        )
                    ) {
                        val firstColumnWidth = maxWidth.times(.10f)
                        val monthColumnWidth = (maxWidth - firstColumnWidth) / 5

                        Surface(
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .height(100.dp)
                                .width(5.dp)
                                .padding(start = firstColumnWidth)
                        ) {
                            Text("")
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            //Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = activityLabel!!,
                                    color = Color(0xFFFFA500),
                                    modifier = Modifier.width(firstColumnWidth),
                                    style = MaterialTheme.typography.caption,
                                    textAlign = TextAlign.Start
                                )

                                MonthTextStat(
                                    viewModel.currentMonth,
                                    monthColumnWidth = monthColumnWidth
                                )

                                Spacer(modifier = Modifier.width(monthColumnWidth))

                                MonthTextStat(
                                    viewModel.previousMonth,
                                    monthColumnWidth = monthColumnWidth
                                )
                                Spacer(modifier = Modifier.width(monthColumnWidth))

                                MonthTextStat(
                                    viewModel.previousPreviousMonth,
                                    monthColumnWidth = monthColumnWidth
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colors.onSurface
                            )
                            // Distance Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                DashboardStat(
                                    image = R.drawable.ic_ruler,
                                    modifier = Modifier.width(firstColumnWidth)
                                )

                                MonthTextStat(
                                    currentMonthMetrics.totalDistance.getDistanceString(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = currentMonthMetrics.totalDistance.toInt(),
                                    then = prevDistance.toInt(),
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Distance
                                )

                                MonthTextStat(
                                    prevDistance.getDistanceString(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = prevDistance.toInt(),
                                    then = prevPrevDistance.toInt(),
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Distance
                                )

                                MonthTextStat(
                                    prevPrevDistance.getDistanceString(),
                                    monthColumnWidth = monthColumnWidth
                                )
                            }
                            //Time Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                DashboardStat(
                                    image = R.drawable.ic_clock_time,
                                    modifier = Modifier.width(firstColumnWidth)
                                )

                                MonthTextStat(
                                    currentMonthMetrics.totalTime.getTimeStringHoursAndMinutes(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = currentMonthMetrics.totalTime,
                                    then = prevTime,
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Time
                                )

                                MonthTextStat(
                                    prevTime.getTimeStringHoursAndMinutes(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = prevTime,
                                    then = prevPrevTime,
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Time
                                )
                                MonthTextStat(
                                    prevPrevTime.getTimeStringHoursAndMinutes(),
                                    monthColumnWidth = monthColumnWidth
                                )
                            }
                            // Elevation Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                DashboardStat(
                                    image = R.drawable.ic_up_right,
                                    modifier = Modifier.width(firstColumnWidth)
                                )

                                MonthTextStat(
                                    currentMonthMetrics.totalElevation.getElevationString(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = currentMonthMetrics.totalElevation.toInt(),
                                    then = prevElevation.toInt(),
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Count
                                )

                                MonthTextStat(
                                    prevElevation.getElevationString(),
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = prevElevation.toInt(),
                                    then = prevPrevElevation.toInt(),
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Count
                                )

                                MonthTextStat(
                                    prevPrevElevation.getElevationString(),
                                    monthColumnWidth = monthColumnWidth
                                )
                            }
                            //Count Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                DashboardStat(
                                    image = R.drawable.ic_hashtag,
                                    modifier = Modifier.width(firstColumnWidth)
                                )

                                MonthTextStat(
                                    "${currentMonthMetrics.count}",
                                    monthColumnWidth = monthColumnWidth
                                )
                                PercentDelta(
                                    now = currentMonthMetrics.count,
                                    then = runCountPrev,
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Count
                                )
                                MonthTextStat(
                                    "$runCountPrev",
                                    monthColumnWidth = monthColumnWidth
                                )

                                PercentDelta(
                                    now = runCountPrev,
                                    then = runCountPrevPrev,
                                    monthColumnWidth = monthColumnWidth,
                                    type = StatType.Count
                                )

                                MonthTextStat(
                                    "$runCountPrevPrev",
                                    monthColumnWidth = monthColumnWidth
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF036e9a)
                ) {

                    BoxWithConstraints(
                        modifier = Modifier.padding(
                            vertical = 12.dp,
                            horizontal = 10.dp
                        )
                    ) {
                        val firstColumnWidth = maxWidth.times(.10f)
                        val monthColumnWidth = (maxWidth - firstColumnWidth) / 5

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            //Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = activityLabel!!,
                                    color = Color(0xFFFFA500),
                                    modifier = Modifier.width(firstColumnWidth),
                                    style = MaterialTheme.typography.caption,
                                    textAlign = TextAlign.Start
                                )

                                MonthTextStat(
                                    viewModel.currentMonth,
                                    monthColumnWidth = monthColumnWidth
                                )

                                Spacer(modifier = Modifier.width(monthColumnWidth))

                                MonthTextStat(
                                    viewModel.previousMonth,
                                    monthColumnWidth = monthColumnWidth
                                )
                                Spacer(modifier = Modifier.width(monthColumnWidth))

                                MonthTextStat(
                                    viewModel.previousPreviousMonth,
                                    monthColumnWidth = monthColumnWidth
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colors.onSurface
                            )

                            if (currentYearActivities != null && prevYearActivities != null && prevPrevYearActivities != null) {
                                Log.d("TAG", "StravaDashboard: ${currentYearActivities.size} ${prevYearActivities.size} ${prevPrevYearActivities.size}")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
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

@Composable
fun DashboardStat(@DrawableRes image: Int, stat: String? = null, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = "",
            modifier = Modifier.size(18.dp),
            tint = Color.LightGray.copy(alpha = .8f)
        )
        stat?.let {
            Text(
                text = stat,
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colors.onSurface,
            )
        }
    }
}

@Composable
fun MonthTextStat(monthStat: String, monthColumnWidth: Dp) {
    Text(
        text = monthStat,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.width(monthColumnWidth),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.caption
    )
}

enum class StatType { Distance, Time, Elevation, Count }

val monthWeekMap: MutableMap<Int, MutableList<Int>> = mutableMapOf()
val today = SimpleDateFormat("dd").format(Calendar.getInstance().time).toInt()
var currentWeek: MutableList<Int> = mutableListOf()
var currentMonthMetrics = MonthMetrics(0, 0f, 0f, 0)

data class MonthMetrics(
    val count: Int,
    val totalDistance: Float,
    val totalElevation: Float,
    val totalTime: Int
)

