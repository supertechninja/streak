package com.mcwilliams.streak.ui.dashboard.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.MonthTextStat
import com.mcwilliams.streak.ui.dashboard.PercentDelta
import com.mcwilliams.streak.ui.dashboard.StatType
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.dashboard.monthWeekMap
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes

@Composable
fun WeekCompareWidget(
    activitesList: List<ActivitesItem>,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
    today: Int?
) {
    StreakWidgetCard(
        content = {
            BoxWithConstraints(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                    horizontal = 10.dp
                )
            ) {
                val firstColumnWidth = maxWidth.times(.12f)
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
                            text = selectedActivityType?.name!!,
                            color = Color(0xFFFFA500),
                            modifier = Modifier.width(firstColumnWidth),
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.ExtraBold,
                        )

                        MonthTextStat(
                            "Current",
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
                            if (selectedActivityType!!.name == ActivityType.All.name) {
                                count = count.inc()
                                distance += activitiesItem.distance
                                elevation += activitiesItem.total_elevation_gain
                                time += activitiesItem.elapsed_time
                            } else if (activitiesItem.type == selectedActivityType!!.name) {
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
                            "StravaDashboard: $count, ${distance.getDistanceString(selectedUnitType!!)}, ${
                                elevation.getElevationString(
                                    selectedUnitType!!
                                )
                            }, ${time.getTimeStringHoursAndMinutes()}"
                        )
                    }

                    // Distance Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashboardStat(
                            image = R.drawable.ic_ruler,
                            modifier = Modifier.width(firstColumnWidth)
                        )

                        MonthTextStat(
                            weeklyDataMap[0].totalDistance.getDistanceString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = weeklyDataMap[0].totalDistance.toInt(),
                            then = weeklyDataMap[1].totalDistance.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Distance
                        )

                        MonthTextStat(
                            weeklyDataMap[1].totalDistance.getDistanceString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = weeklyDataMap[1].totalDistance.toInt(),
                            then = weeklyDataMap[2].totalDistance.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Distance
                        )

                        MonthTextStat(
                            weeklyDataMap[2].totalDistance.getDistanceString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )
                    }
                    //Time Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            modifier = Modifier.width(firstColumnWidth)
                        )

                        MonthTextStat(
                            weeklyDataMap[0].totalElevation.getElevationString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = weeklyDataMap[0].totalElevation.toInt(),
                            then = weeklyDataMap[1].totalElevation.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )

                        MonthTextStat(
                            weeklyDataMap[1].totalElevation.getElevationString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = weeklyDataMap[1].totalElevation.toInt(),
                            then = weeklyDataMap[2].totalElevation.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )

                        MonthTextStat(
                            weeklyDataMap[2].totalElevation.getElevationString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )
                    }
                    //Count Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
        })
}