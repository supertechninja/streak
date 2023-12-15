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
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getAveragePaceString
import com.mcwilliams.streak.ui.utils.getBarHeight
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeString
import java.time.LocalDate
import java.time.Month

@Composable
fun YearSummaryWidget(
    yearDistanceByMonth: List<Pair<Int, Double>>,
    isLoading: Boolean,
    yearMetrics: SummaryMetrics,
    selectedActivityType: ActivityType,
    selectedUnitType: UnitType,
) {
    Log.d("TAG", "YearSummaryWidget: $yearDistanceByMonth")

    StreakWidgetCard(
        content = {
            BoxWithConstraints(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                    horizontal = 10.dp
                )
            ) {
                val graphWidth = this.maxWidth.times(.6f)
                val dataWidth = this.maxWidth.times(.4f)

                Row() {
                    Column(
                        modifier = Modifier.width(width = dataWidth),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row() {
                            //TODO String Builder
                            Text(
                                text = "Run | ",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${LocalDate.now().year}",
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
                            stat = yearMetrics.totalDistance.getDistanceString(
                                selectedUnitType,
                                true
                            ),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_clock_time,
                            stat = yearMetrics.totalTime.getTimeString(),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            stat = yearMetrics.totalElevation.getElevationString(selectedUnitType),
                            isLoading = isLoading
                        )

                        DashboardStat(
                            image = R.drawable.ic_speed,
                            stat = getAveragePaceString(
                                yearMetrics.totalDistance,
                                yearMetrics.totalTime,
                                selectedUnitType
                            ),
                            isLoading = isLoading
                        )
                    }

                    //Vertical Bars Representing Miles Per Day
                    Column(
                        modifier = Modifier
                            .width(width = graphWidth)
                            .height(140.dp)
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (progress, months) = createRefs()

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.constrainAs(progress) {
                                    bottom.linkTo(months.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                                yearDistanceByMonth.forEach { (monthInt, distance) ->
                                    Column(
                                        modifier = Modifier.width(graphWidth / 12),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (distance > 0.0) {
                                            Divider(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier
                                                    .height(distance.getBarHeight())
                                                    .width(6.dp)
                                                    .clip(RoundedCornerShape(50))
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.constrainAs(months) {
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                                Month.values().forEach {
                                    Column(
                                        modifier = Modifier.width(graphWidth / 12),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            it.name.substring(0, 1),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (it.name == LocalDate.now().month.name) FontWeight.ExtraBold else FontWeight.Normal
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