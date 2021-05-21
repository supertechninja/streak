package com.mcwilliams.streak.ui.dashboard.widgets

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.dashboard.monthWeekMap
import com.mcwilliams.streak.ui.dashboard.today
import com.mcwilliams.streak.ui.theme.primaryColor
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import java.time.YearMonth
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MonthWidget(
    monthlyWorkouts: List<ActivitesItem>,
    updateMonthlyMetrics: (SummaryMetrics) -> Unit,
    selectedActivityType: ActivityType?,
    weekCount: Int,
    firstDayOffset: Int,
    lastDayCount: Int,
    priorMonthLength: Int,
    currentWeek: (MutableList<Int>) -> Unit,
    selectedUnitType: UnitType?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = primaryColor
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

            updateMonthlyMetrics(
                SummaryMetrics(
                    count = count,
                    totalDistance = totalDistance,
                    totalElevation = totalElevation,
                    totalTime = totalTime
                )
            )

            Row() {
                Column(
                    modifier = Modifier.width(width = width),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row() {
                        Text(
                            selectedActivityType?.name!!,
                            color = Color(0xFFFFA500),
                        )
                        Text(
                            "  |  ${
                                YearMonth.now().month.name.toLowerCase(Locale.getDefault())
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
                        stat = totalDistance.getDistanceString(selectedUnitType!!)
                    )

                    DashboardStat(
                        image = R.drawable.ic_clock_time,
                        stat = totalTime.getTimeStringHoursAndMinutes()
                    )

                    DashboardStat(
                        image = R.drawable.ic_up_right,
                        stat = totalElevation.getElevationString(selectedUnitType!!)
                    )

                    DashboardStat(
                        image = R.drawable.ic_hashtag,
                        stat = "$count"
                    )
                }
                Column(modifier = Modifier.width(width = width)) {
                    for (i in 0..weekCount) {
                        CalendarView(
                            startDayOffSet = firstDayOffset,
                            endDayCount = lastDayCount,
                            monthWeekNumber = i,
                            priorMonthLength = priorMonthLength,
                            weekCount = weekCount,
                            width = width,
                            daysActivitiesLogged = listOfDaysLoggedActivity,
                            currentWeek = currentWeek
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
                    Log.d("TAG", "StravaDashboard: $monthWeekMap")
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarView(
    startDayOffSet: Int,
    endDayCount: Int,
    monthWeekNumber: Int,
    priorMonthLength: Int,
    weekCount: Int,
    width: Dp,
    daysActivitiesLogged: MutableList<Int>,
    currentWeek: (MutableList<Int>) -> Unit,
) {
    val dateModifier = Modifier.width(width = width / 7)
    Row(modifier = Modifier.fillMaxWidth()) {

        val listOfDatesInWeek: MutableList<Int> = mutableListOf()

        if (monthWeekNumber == 0) {
            for (i in 0 until startDayOffSet) {
                val priorDay = (priorMonthLength - (startDayOffSet - i - 1))
                Log.d("TAG", "CalendarView: $priorDay")

                listOfDatesInWeek.add(priorDay)

                Text(
                    " ",
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = dateModifier
                )
            }
        }

        val endDay = when (monthWeekNumber) {
            0 -> 7 - startDayOffSet
            weekCount -> endDayCount
            else -> 7
        }

        for (i in 1..endDay) {
            val day =
                if (monthWeekNumber == 0) i else (i + (7 * monthWeekNumber) - startDayOffSet)

            val dayColor =
                when {
                    daysActivitiesLogged.contains(day) -> Color(0xFFFFA500)
                    day < today -> MaterialTheme.colors.onSurface
                    day == today -> MaterialTheme.colors.onSurface
                    else -> Color.LightGray.copy(alpha = .8f)
                }

            Row() {
                Surface(
                    color = Color.Transparent,
                    shape = CircleShape,
                    border = if (day == today) BorderStroke(
                        1.dp,
                        color = MaterialTheme.colors.onSurface
                    ) else null
                ) {
                    Text(
                        "$day",
                        textAlign = TextAlign.Center,
                        modifier = dateModifier.padding(2.dp),
                        fontWeight = FontWeight.Medium,
                        color = dayColor,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            listOfDatesInWeek.add(day)
        }

        if (listOfDatesInWeek.contains(today)) {
            currentWeek(listOfDatesInWeek)
//            currentWeek = listOfDatesInWeek
        }

        monthWeekMap.put(monthWeekNumber, listOfDatesInWeek)
        Log.d("TAG", "CalendarView: $monthWeekMap")
    }
}