package com.mcwilliams.streak.ui.dashboard.widgets

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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getAveragePaceString
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import java.time.YearMonth
import java.util.Locale

@Composable
fun MonthWidget(
    monthlyWorkouts: List<ActivitiesItem>,
    updateMonthlyMetrics: (SummaryMetrics) -> Unit,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
    monthWeekMap: MutableMap<Int, MutableList<Pair<Int, Int>>>,
    today: Int?,
    isLoading: Boolean,
) {
    val currentMonth = YearMonth.now().month

    StreakWidgetCard(
        content = {
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

                        totalTime += it.moving_time

                        totalDistance += it.distance

                        count = count.inc()
                    } else if (it.type == selectedActivityType!!.name) {
                        val date = it.start_date_local.getDate()
                        listOfDaysLoggedActivity.add(date.dayOfMonth)

                        totalElevation += it.total_elevation_gain

                        totalTime += it.moving_time

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
                                text = selectedActivityType?.name ?: "",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "  |  ${
                                    currentMonth.name.lowercase(Locale.getDefault())
                                        .capitalize(Locale.getDefault())
                                }",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(vertical = 4.dp)
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        DashboardStat(
                            image = R.drawable.ic_ruler,
                            stat = totalDistance.getDistanceString(selectedUnitType!!),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_clock_time,
                            stat = totalTime.getTimeStringHoursAndMinutes(),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            stat = totalElevation.getElevationString(selectedUnitType),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_speed,
                            stat = getAveragePaceString(
                                totalDistance,
                                totalTime,
                                selectedUnitType
                            ),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_hashtag,
                            stat = "$count",
                            isLoading = isLoading
                        )
                    }
                    Column(modifier = Modifier.width(width = width)) {

                        val dateModifier = Modifier.width(width = width / 7)

                        for (weekCount in 0..monthWeekMap.size - 2) {
                            val week = monthWeekMap[weekCount]

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {

                                week?.forEach {
                                    Row() {
                                        var dayColor = Color.Transparent
                                        if (it.first == YearMonth.now().monthValue) {
                                            dayColor =
                                                when {
                                                    listOfDaysLoggedActivity.contains(it.second) -> MaterialTheme.colorScheme.primary
                                                    it.second < today!! -> MaterialTheme.colorScheme.onSurface
                                                    it.second == today -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                                                }
                                        }

                                        Surface(
                                            color = Color.Transparent,
                                            shape = CircleShape,
                                            border = if (it.first == YearMonth.now().monthValue && it.second == today) BorderStroke(
                                                1.dp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ) else null
                                        ) {
                                            Text(
                                                "${it.second}",
                                                textAlign = TextAlign.Center,
                                                modifier = dateModifier.padding(2.dp),
                                                fontWeight = FontWeight.Medium,
                                                color = dayColor,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
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