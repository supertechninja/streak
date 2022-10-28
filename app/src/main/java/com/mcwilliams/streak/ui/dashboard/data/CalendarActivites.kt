package com.mcwilliams.streak.ui.dashboard.data

import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getAveragePaceString
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

data class CalendarActivities(
    val currentMonthActivities: List<ActivitiesItem> = emptyList(),
    val previousMonthActivities: List<ActivitiesItem> = emptyList(),
    val twoMonthAgoActivities: List<ActivitiesItem> = emptyList(),
    val currentYearActivities: List<ActivitiesItem> = emptyList(),
    val previousYearActivities: List<ActivitiesItem> = emptyList(),
    val twoYearsAgoActivities: List<ActivitiesItem> = emptyList(),
    val preferredActivityType: ActivityType,
    val selectedUnitType: UnitType
) {
    val lastTwoMonthsActivities: List<ActivitiesItem> =
        currentMonthActivities.plus(previousMonthActivities)

    val calendarData = CalendarData()

    val weeklyDistanceMap: Pair<SummaryInfo, MutableMap<Int, Int>> = loadWeeklyDistanceMap()

    private fun loadWeeklyDistanceMap(): Pair<SummaryInfo, MutableMap<Int, Int>> {
        val dayOfWeekWithDistance: MutableMap<Int, Int> = mutableMapOf()

        var totalDistance = 0f
        var totalElevation = 0f
        var totalTime = 0
        var count = 0

        lastTwoMonthsActivities.forEach { activitesItem ->
            val date = activitesItem.start_date_local.getDate()

            calendarData.currentWeek.forEach {
                if (it.second == date.dayOfMonth && it.first == date.monthValue) {
                    if (preferredActivityType.name == ActivityType.All.name) {
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
                    } else if (activitesItem.type == preferredActivityType.name) {
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

        val weeklySummaryInfo = SummaryInfo(
            widgetTitle = " | ${
                Month.of(calendarData.currentWeek[0].first).getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.getDefault()
                )} ${calendarData.currentWeek[0].second}-" +
                    "${
                        Month.of(calendarData.currentWeek.last().first).getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        )
                    }  ${calendarData.currentWeek.last().second}",
            distance = totalDistance.getDistanceString(selectedUnitType),
            totalTime = totalTime.getTimeStringHoursAndMinutes(),
            elevation = totalElevation.getElevationString(selectedUnitType),
            avgPace = getAveragePaceString(totalDistance, totalTime, selectedUnitType),
            totalCount = ""
        )

        return weeklySummaryInfo to dayOfWeekWithDistance
    }
}

data class SummaryInfo(
    val widgetTitle: String,
    val distance: String,
    val elevation: String,
    val totalTime: String,
    val avgPace: String,
    val totalCount: String
)