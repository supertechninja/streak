package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
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
    val actualWeekCount = weekCount - 2

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

        val error by viewModel.error.observeAsState()

        val activityLabel = selectedActivityType?.name

        val context = LocalContext.current

        Scaffold(
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
                            onClick = { fetchData = 0 },
                            modifier = Modifier.constrainAs(action) {
                                end.linkTo(parent.end, 16.dp)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }

                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = paddingValues)
                        .verticalScroll(rememberScrollState())
                        .background(color = Color(0xFF01374D))
                ) {

                    error?.let {
                        if (it.isNotEmpty()) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }

                    monthlyActivities?.let { monthlyWorkouts ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .padding(horizontal = 16.dp),
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

                                var listOfDaysLoggedActivity = mutableListOf<Int>()
                                var totalDistance = 0f
                                var totalElevation = 0f
                                var totalTime = 0
                                var count = 0

                                monthlyWorkouts.forEach {
                                    if (selectedActivityType!!.name == ActivityType.All.name) {
                                        val date = it.start_date_local.getDate()
                                        listOfDaysLoggedActivity.add(date.dayOfMonth)

                                        totalElevation += it.total_elevation_gain

                                        totalTime += it.elapsed_time

                                        totalDistance += it.distance

                                        count = count.inc()
                                    } else if (it.type == selectedActivityType!!.name) {
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

                                currentMonthMetrics = SummaryMetrics(
                                    count = count,
                                    totalDistance = totalDistance,
                                    totalElevation = totalElevation,
                                    totalTime = totalTime
                                )

                                Row() {
                                    Column(
                                        modifier = Modifier.width(width = width),
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
                                                .height(1.dp),
                                            color = MaterialTheme.colors.onSurface
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

                                        DashboardStat(
                                            image = R.drawable.ic_hashtag,
                                            stat = "$count"
                                        )
                                    }
                                    Column(modifier = Modifier.width(width = width)) {
                                        monthlyActivities?.let {
                                            for (i in 0..weekCount) {
                                                CalendarView(
                                                    startDayOffSet = firstDayOffset,
                                                    endDayCount = lastDayCount,
                                                    monthWeekNumber = i,
                                                    priorMonthLength = priorMonthLength,
                                                    weekCount = weekCount,
                                                    width = width,
                                                    daysActivitiesLogged = listOfDaysLoggedActivity,
                                                    actualWeekCount = actualWeekCount
                                                )
                                            }

                                            //Add previous 2 weeks to week map
                                            val firstDayWeekZeroMonth =
                                                (priorMonthLength - (firstDayOffset - 1))

                                            val listOfDatesInPreviousWeek: MutableList<Int> =
                                                mutableListOf()

                                            for (i in 0..6) {
                                                val priorDay = (firstDayWeekZeroMonth - (i + 1))
                                                listOfDatesInPreviousWeek.add(priorDay)
                                            }
                                            monthWeekMap.put(-1, listOfDatesInPreviousWeek)

                                            val listOfDatesInTwoWeeksAgo: MutableList<Int> =
                                                mutableListOf()
                                            val twoWeekAgo = firstDayWeekZeroMonth - 7
                                            for (i in 0..6) {
                                                val priorDay = (twoWeekAgo - (i + 1))
                                                listOfDatesInTwoWeeksAgo.add(priorDay)
                                            }
                                            monthWeekMap.put(-2, listOfDatesInTwoWeeksAgo)
                                        }
                                        Log.d("TAG", "StravaDashboard: $monthWeekMap")
                                    }
                                }
                            }
                        }


                        last2MonthsActivities?.let { activitesList ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .padding(horizontal = 16.dp),
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
                                                "This week",
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            Spacer(modifier = Modifier.width(monthColumnWidth))

                                            MonthTextStat(
                                                "1w ago",
                                                monthColumnWidth = monthColumnWidth
                                            )
                                            Spacer(modifier = Modifier.width(monthColumnWidth))

                                            MonthTextStat(
                                                "2w ago",
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

                                        val weeklyDataMap: MutableList<SummaryMetrics> =
                                            mutableListOf()

                                        val weeklyActivitiesMap: MutableList<Pair<Int, MutableList<ActivitesItem>>> =
                                            mutableListOf()

                                        var startingWeekInMap: MutableList<Int> = mutableListOf()
                                        monthWeekMap.forEach loop@{ weekCount, datesInWeek ->
                                            if (datesInWeek.contains(today)) {
                                                startingWeekInMap.add(weekCount)
                                            }
                                        }

                                        for (i in startingWeekInMap[0] downTo (startingWeekInMap[0] - 2)) {
                                            val weeklyActivitiesList =
                                                mutableListOf<ActivitesItem>()
                                            activitesList.forEach { activitiesItem ->
                                                val datesInWeek = monthWeekMap.get(i)
                                                if (datesInWeek!!.contains(activitiesItem.start_date_local.getDate().dayOfMonth)) {
                                                    weeklyActivitiesList.add(activitiesItem)
                                                }
                                            }
                                            weeklyActivitiesMap.add(Pair(i, weeklyActivitiesList))
                                        }


                                        weeklyActivitiesMap.forEach { weeklyActivityMap ->
                                            var count = 0
                                            var distance = 0f
                                            var elevation = 0f
                                            var time = 0
                                            weeklyActivityMap.second.forEach { activitiesItem ->
                                                if (activitiesItem.type == selectedActivityType!!.name) {
                                                    count = count.inc()
                                                    distance += activitiesItem.distance
                                                    elevation += activitiesItem.total_elevation_gain
                                                    time += activitiesItem.elapsed_time
                                                }
                                            }

                                            weeklyDataMap.add(
                                                SummaryMetrics(
                                                    count = count,
                                                    totalDistance = distance,
                                                    totalElevation = elevation,
                                                    totalTime = time
                                                )
                                            )
                                            Log.d(
                                                "TAG",
                                                "StravaDashboard: $count, ${distance.getDistanceString()}, ${elevation.getElevationString()}, ${time.getTimeStringHoursAndMinutes()}"
                                            )
                                        }

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
                                                weeklyDataMap[0].totalDistance.getDistanceString(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[0].totalDistance.toInt(),
                                                then = weeklyDataMap[1].totalDistance.toInt(),
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Distance
                                            )

                                            MonthTextStat(
                                                weeklyDataMap[1].totalDistance.getDistanceString(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[1].totalDistance.toInt(),
                                                then = weeklyDataMap[2].totalDistance.toInt(),
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Distance
                                            )

                                            MonthTextStat(
                                                weeklyDataMap[2].totalDistance.getDistanceString(),
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
                                                weeklyDataMap[0].totalTime.getTimeStringHoursAndMinutes(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[0].totalTime,
                                                then = weeklyDataMap[1].totalTime,
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Time
                                            )

                                            MonthTextStat(
                                                weeklyDataMap[1].totalTime.getTimeStringHoursAndMinutes(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[1].totalTime,
                                                then = weeklyDataMap[2].totalTime,
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Time
                                            )
                                            MonthTextStat(
                                                weeklyDataMap[2].totalTime.getTimeStringHoursAndMinutes(),
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
                                                weeklyDataMap[0].totalElevation.getElevationString(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[0].totalElevation.toInt(),
                                                then = weeklyDataMap[1].totalElevation.toInt(),
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Count
                                            )

                                            MonthTextStat(
                                                weeklyDataMap[1].totalElevation.getElevationString(),
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[1].totalElevation.toInt(),
                                                then = weeklyDataMap[2].totalElevation.toInt(),
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Count
                                            )

                                            MonthTextStat(
                                                weeklyDataMap[2].totalElevation.getElevationString(),
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
                                                "${weeklyDataMap[0].count}",
                                                monthColumnWidth = monthColumnWidth
                                            )
                                            PercentDelta(
                                                now = weeklyDataMap[0].count,
                                                then = weeklyDataMap[1].count,
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Count
                                            )
                                            MonthTextStat(
                                                "${weeklyDataMap[1].count}",
                                                monthColumnWidth = monthColumnWidth
                                            )

                                            PercentDelta(
                                                now = weeklyDataMap[1].count,
                                                then = weeklyDataMap[2].count,
                                                monthColumnWidth = monthColumnWidth,
                                                type = StatType.Count
                                            )

                                            MonthTextStat(
                                                "${weeklyDataMap[2].count}",
                                                monthColumnWidth = monthColumnWidth
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        monthlyActivities?.let { monthlyWorkouts ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .padding(horizontal = 16.dp),
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
                                                    .height(1.dp),
                                                color = MaterialTheme.colors.onSurface
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

                                            DashboardStat(
                                                image = R.drawable.ic_hashtag,
                                                stat = "$count"
                                            )
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
                                                                                color = Color(
                                                                                    0xFFFFA500
                                                                                ),
                                                                                modifier = Modifier
                                                                                    .height(
                                                                                        progressHeight
                                                                                    )
                                                                                    .width(15.dp)
                                                                                    .clip(
                                                                                        RoundedCornerShape(
                                                                                            50
                                                                                        )
                                                                                    )
                                                                                    .padding(
                                                                                        horizontal = 4.dp
                                                                                    )
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
                                                                Text(it.name.substring(0, 1), color = MaterialTheme.colors.onSurface)
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
                                    .padding(8.dp)
                                    .padding(horizontal = 16.dp),
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
            })
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
var currentMonthMetrics = SummaryMetrics(0, 0f, 0f, 0)

data class SummaryMetrics(
    val count: Int,
    val totalDistance: Float,
    val totalElevation: Float,
    val totalTime: Int
)

