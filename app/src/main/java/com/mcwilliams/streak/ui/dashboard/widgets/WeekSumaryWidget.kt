package com.mcwilliams.streak.ui.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.data.SummaryInfo
import com.mcwilliams.streak.ui.utils.getBarHeight
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeekSummaryWidget(
    weeklyDistanceMap: Pair<SummaryInfo, MutableMap<Int, Int>>,
    currentWeeklyInfo: MutableList<Pair<Int, Int>>,
    saveWeeklyStats: (String, String) -> Unit,
    isLoading: Boolean,
) {
    StreakWidgetCard(
        content = {
            BoxWithConstraints(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                    horizontal = 10.dp
                )
            ) {
                val width = this.maxWidth / 2
                val summaryInfo = weeklyDistanceMap.first

                Row() {
                    Column(
                        modifier = Modifier.width(width = width),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row() {
                            //TODO String Builder
                            Text(
                                text = "Run",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = summaryInfo.widgetTitle,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
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
                            stat = summaryInfo.distance,
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_clock_time,
                            stat = summaryInfo.totalTime,
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            stat = summaryInfo.elevation,
                            isLoading = isLoading
                        )

                        saveWeeklyStats(summaryInfo.distance, summaryInfo.elevation)

                        DashboardStat(
                            image = R.drawable.ic_speed,
                            stat = summaryInfo.avgPace,
                            isLoading = isLoading
                        )
                    }

                    //Vertical Bars Representing Miles Per Day
                    Column(
                        modifier = Modifier
                            .width(width = width)
                            .height(120.dp)
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (progress, days) = createRefs()

                            val currentWeekDistanceByDay = weeklyDistanceMap.second

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.constrainAs(progress) {
                                    bottom.linkTo(days.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                                currentWeeklyInfo.forEach { dateInWeek ->
                                    Column(
                                        modifier = Modifier.width(width / 7),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        currentWeekDistanceByDay.forEach { weekDistanceMap ->
                                            if (weekDistanceMap.key == dateInWeek.second) {
                                                if (weekDistanceMap.value > 0) {
                                                    Divider(
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier
                                                            .height(weekDistanceMap.value.getBarHeight())
                                                            .width(15.dp)
                                                            .clip(RoundedCornerShape(50))
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