package com.mcwilliams.streak.ui.dashboard.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekSummaryWidget(
    monthlyWorkouts: List<ActivitiesItem>,
    selectedActivityType: ActivityType?,
    currentWeek: MutableList<Pair<Int, Int>>,
    selectedUnitType: UnitType?,
    today: Int,
    isLoading: Boolean,
    saveWeeklyStats: (String, String) -> Unit,
) {
    StreakWidgetCard(
        content = {
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

                    currentWeek.forEach {
                        if (it.second == date.dayOfMonth && it.first == date.monthValue) {
                            if (selectedActivityType!!.name == ActivityType.All.name) {
                                totalElevation += activitesItem.total_elevation_gain

                                totalTime += activitesItem.moving_time

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

                                totalTime += activitesItem.moving_time

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

                }

                Row() {
                    Column(
                        modifier = Modifier.width(width = width),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row() {
                            Text(
                                text = selectedActivityType?.name ?: "",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (currentWeek.isNotEmpty()) {
                                val text =
                                    " | ${
                                        Month.of(currentWeek[0].first).getDisplayName(
                                            TextStyle.SHORT_STANDALONE,
                                            Locale.getDefault()
                                        )
                                    } ${currentWeek[0].second}-" +
                                            "${
                                                Month.of(currentWeek.last().first).getDisplayName(
                                                    TextStyle.SHORT_STANDALONE,
                                                    Locale.getDefault()
                                                )
                                            }  ${currentWeek.last().second}"
                                Text(
                                    text = text,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(vertical = 4.dp)
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val weeklyDistance = totalDistance.getDistanceString(selectedUnitType!!)
                        val weeklyElevation = totalElevation.getElevationString(selectedUnitType!!)

                        DashboardStat(
                            image = R.drawable.ic_ruler,
                            stat = weeklyDistance,
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_clock_time,
                            stat = totalTime.getTimeStringHoursAndMinutes(),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            stat = weeklyElevation,
                            isLoading = isLoading
                        )
                        saveWeeklyStats(weeklyDistance, weeklyElevation)

                        DashboardStat(
                            image = R.drawable.ic_speed,
                            stat = getAveragePaceString(totalDistance, totalTime, selectedUnitType!!),
                            isLoading = isLoading
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(width = width)
                            .height(120.dp)
                    ) {
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
                                            if (it.key == dateInWeek.second) {
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
                                                        color = MaterialTheme.colorScheme.onSurface,
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
                                        Text(
                                            it.name.substring(0, 1),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (it.name == LocalDate.now().dayOfWeek.name) FontWeight.ExtraBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}