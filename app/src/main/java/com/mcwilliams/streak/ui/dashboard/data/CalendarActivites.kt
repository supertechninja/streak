package com.mcwilliams.streak.ui.dashboard.data

import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.MeasureType
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.dashboard.getStats
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
    val selectedUnitType: UnitType,
    val preferredMeasureType: MeasureType,
    val relativePrevPrevMonthActivities: List<ActivitiesItem> = emptyList(),
    val relativePreviousMonthActivities: List<ActivitiesItem> = emptyList(),
    val relativeYearActivities: List<ActivitiesItem> = emptyList(),
    val relativePreviousYearActivities: List<ActivitiesItem> = emptyList(),
    val relativeTwoYearsAgoActivities: List<ActivitiesItem> = emptyList(),
    val relativeMonthActivities: List<ActivitiesItem> = emptyList()
) {
    val lastTwoMonthsActivities: List<ActivitiesItem> =
        currentMonthActivities.plus(previousMonthActivities)

    val calendarData = CalendarData()

    val weeklyDistanceMap: Pair<SummaryInfo, MutableMap<Int, Int>> = loadWeeklyDistanceMap()

    val yearlySummaryMetrics = buildList {
        if (preferredMeasureType == MeasureType.Absolute) {
            add(currentYearActivities.getStats(preferredActivityType))
            add(previousYearActivities.getStats(preferredActivityType))
            add(twoYearsAgoActivities.getStats(preferredActivityType))
        } else {
            add(relativeYearActivities.getStats(preferredActivityType))
            add(relativePreviousYearActivities.getStats(preferredActivityType))
            add(relativeTwoYearsAgoActivities.getStats(preferredActivityType))
        }
    }

    val yearMetrics =  if (preferredMeasureType == MeasureType.Absolute) {
        currentYearActivities.getStats(preferredActivityType)
    } else {
        relativeYearActivities.getStats(preferredActivityType)
    }

    val monthlySummaryMetrics = buildList {
        if (preferredMeasureType == MeasureType.Absolute) {
            add(currentMonthActivities.getStats(preferredActivityType))
            add(previousMonthActivities.getStats(preferredActivityType))
            add(twoMonthAgoActivities.getStats(preferredActivityType))
        } else {
            add(relativeMonthActivities.getStats(preferredActivityType))
            add(relativePreviousMonthActivities.getStats(preferredActivityType))
            add(relativePrevPrevMonthActivities.getStats(preferredActivityType))
        }
    }

    lateinit var weeklyActivityIds: MutableList<Long>

    private fun loadWeeklyDistanceMap(): Pair<SummaryInfo, MutableMap<Int, Int>> {
        val activitiesForTheWeek = mutableListOf<ActivitiesItem>()
        calendarData.currentWeek.forEach { (monthInt, dayOfMonth) ->
            activitiesForTheWeek.addAll(
                lastTwoMonthsActivities.filter { activity ->
                    val date = activity.start_date.getDate()
                    //Filter out activities by the current month and day of month with type
                    if (preferredActivityType.name == ActivityType.All.name) {
                        date.monthValue == monthInt && date.dayOfMonth == dayOfMonth
                    } else {
                        date.monthValue == monthInt && date.dayOfMonth == dayOfMonth
                                && preferredActivityType.name == activity.type
                    }
                })
        }

        val totalWeeklyDistance = activitiesForTheWeek.sumOf { it.distance.toDouble() }.toFloat()
        val totalWeeklyTime = activitiesForTheWeek.sumOf { it.moving_time }

        val weeklySummaryInfoData = SummaryInfo(
            widgetTitle = " | ${
                Month.of(calendarData.currentWeek[0].first).getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.getDefault()
                )
            } ${calendarData.currentWeek[0].second}-" +
                    "${
                        Month.of(calendarData.currentWeek.last().first).getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        )
                    }  ${calendarData.currentWeek.last().second}",
            distance = totalWeeklyDistance.getDistanceString(selectedUnitType),
            totalTime = totalWeeklyTime.getTimeStringHoursAndMinutes(),
            elevation = activitiesForTheWeek
                .filter { it.type == preferredActivityType.name }
                .sumOf { it.total_elevation_gain.toDouble() }.toFloat()
                .getElevationString(selectedUnitType),
            avgPace = getAveragePaceString(totalWeeklyDistance, totalWeeklyTime, selectedUnitType),
            totalCount = activitiesForTheWeek
                .count().toString()
        )

        //Build map of dayOfMonth to distance
        val distanceByDay = activitiesForTheWeek.associateBy({
            it.start_date.getDate().dayOfMonth
        }, { activity ->
            activity.distance.toInt()
        })

        //List of weeklyActivityIds
        weeklyActivityIds =
            activitiesForTheWeek
                .filter { it.type == preferredActivityType.name }
                .map { it.id }
                .toMutableList()

        return weeklySummaryInfoData to distanceByDay.toMutableMap()
    }
}

data class SummaryInfo(
    val widgetTitle: String,
    val distance: String,
    val elevation: String,
    val totalTime: String,
    val avgPace: String,
    val totalCount: String,
)