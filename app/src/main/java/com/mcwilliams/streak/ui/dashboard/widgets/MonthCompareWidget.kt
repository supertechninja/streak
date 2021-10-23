package com.mcwilliams.streak.ui.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.MonthTextStat
import com.mcwilliams.streak.ui.dashboard.PercentDelta
import com.mcwilliams.streak.ui.dashboard.StatType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.theme.primaryColor
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes

@Composable
fun CompareWidget(
    dashboardType: DashboardType,
    selectedActivityType: ActivityType?,
    columnTitles: Array<String>,
    prevMetrics: SummaryMetrics,
    prevPrevMetrics: SummaryMetrics,
    currentMonthMetrics: SummaryMetrics,
    selectedUnitType: UnitType?
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
                            text = selectedActivityType?.name ?: "",
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.width(firstColumnWidth),
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.ExtraBold
                        )

                        MonthTextStat(
                            columnTitles[0],
                            monthColumnWidth = monthColumnWidth
                        )

                        Spacer(modifier = Modifier.width(monthColumnWidth))

                        MonthTextStat(
                            columnTitles[1],
                            monthColumnWidth = monthColumnWidth
                        )
                        Spacer(modifier = Modifier.width(monthColumnWidth))

                        MonthTextStat(
                            columnTitles[2],
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashboardStat(
                            image = R.drawable.ic_ruler,
                            modifier = Modifier.width(firstColumnWidth)
                        )

                        MonthTextStat(
                            currentMonthMetrics.totalDistance.getDistanceString(
                                selectedUnitType!!,
                                isYearSummary = dashboardType == DashboardType.Year
                            ),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = currentMonthMetrics.totalDistance.toInt(),
                            then = prevMetrics.totalDistance.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Distance
                        )

                        MonthTextStat(
                            prevMetrics.totalDistance.getDistanceString(
                                selectedUnitType!!,
                                isYearSummary = dashboardType == DashboardType.Year
                            ),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = prevMetrics.totalDistance.toInt(),
                            then = prevPrevMetrics.totalDistance.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Distance
                        )

                        MonthTextStat(
                            prevPrevMetrics.totalDistance.getDistanceString(
                                selectedUnitType!!,
                                isYearSummary = dashboardType == DashboardType.Year
                            ),
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
                            currentMonthMetrics.totalTime.getTimeStringHoursAndMinutes(),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = currentMonthMetrics.totalTime,
                            then = prevMetrics.totalTime,
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Time
                        )

                        MonthTextStat(
                            prevMetrics.totalTime.getTimeStringHoursAndMinutes(),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = prevMetrics.totalTime,
                            then = prevPrevMetrics.totalTime,
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Time
                        )
                        MonthTextStat(
                            prevPrevMetrics.totalTime.getTimeStringHoursAndMinutes(),
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
                            currentMonthMetrics.totalElevation.getElevationString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = currentMonthMetrics.totalElevation.toInt(),
                            then = prevMetrics.totalElevation.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )

                        MonthTextStat(
                            prevMetrics.totalElevation.getElevationString(selectedUnitType!!),
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = prevMetrics.totalElevation.toInt(),
                            then = prevPrevMetrics.totalElevation.toInt(),
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )

                        MonthTextStat(
                            prevPrevMetrics.totalElevation.getElevationString(selectedUnitType!!),
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
                            "${currentMonthMetrics.count}",
                            monthColumnWidth = monthColumnWidth
                        )
                        PercentDelta(
                            now = currentMonthMetrics.count,
                            then = prevMetrics.count,
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )
                        MonthTextStat(
                            "${prevMetrics.count}",
                            monthColumnWidth = monthColumnWidth
                        )

                        PercentDelta(
                            now = prevMetrics.count,
                            then = prevPrevMetrics.count,
                            monthColumnWidth = monthColumnWidth,
                            type = StatType.Count
                        )

                        MonthTextStat(
                            "${prevPrevMetrics.count}",
                            monthColumnWidth = monthColumnWidth
                        )
                    }
                }
            }
        })
}

enum class DashboardType { Week, Month, Year }