package com.mcwilliams.streak.ui.dashboard.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.round

@Composable
fun WeeklySnapshot(
    selectedActivityType: ActivityType?,
    currentWeek: MutableList<Pair<Int, Int>>,
    selectedUnitType: UnitType?,
    weeklySummaryMetrics: SummaryMetrics,
    dayOfWeekWithDistance: MutableMap<Int, Int>,
//    weeklyGoal: String?,
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

                Text(
                    text = selectedActivityType?.name!!,
                    color = Color(0xFFFFA500),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.body2
                )

                Row() {
                    Column(
                        modifier = Modifier.width(width = width),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = weeklySummaryMetrics.totalDistance.getDistanceString(
                                selectedUnitType = selectedUnitType!!
                            ),
                            color = MaterialTheme.colors.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                        )
//                        weeklyGoal?.let { weeklyGoal ->
//                            if(weeklyGoal.isNotEmpty()) {
//                                selectedUnitType.let {
//                                    Text(
//                                        text = "/ $weeklyGoal ${if (selectedUnitType == UnitType.Imperial) " mi" else " km"}",
//                                        color = MaterialTheme.colors.onSurface,
//                                        fontWeight = FontWeight.ExtraBold,
//                                        style = MaterialTheme.typography.h6,
//                                        textAlign = TextAlign.End,
//                                        modifier = Modifier.padding(end = 16.dp).fillMaxWidth()
//                                    )
//                                }
//                            }
//                        }

                    }

                    Column(
                        modifier = Modifier.width(width = width).padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Elevation: ${
                                weeklySummaryMetrics.totalElevation.getElevationString(
                                    selectedUnitType = selectedUnitType!!
                                )
                            }",
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        val averagePace = (60 / weeklySummaryMetrics.averageSpeed)
                        Log.d("TAG", "WeeklySnapshot: $averagePace")
                        val remainder = (averagePace - averagePace.toInt())
                        val minutes = remainder * 60

                        Text(
                            text = "Avg Pace: ${averagePace.toInt()}m ${minutes.toInt()}s",
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )



                        Text(
                            text = "Avg Speed: ${weeklySummaryMetrics.averageSpeed.round(1)} mph",
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    )
}