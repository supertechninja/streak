package com.mcwilliams.streak.ui.dashboard.widgets

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getAveragePaceString
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString

@Composable
fun YearWidget(
    yearMetrics: SummaryMetrics,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
    isLoading: Boolean,
) {
    StreakWidgetCard(
        content = {
            BoxWithConstraints(
                modifier = Modifier
                    .padding(
                        vertical = 12.dp,
                        horizontal = 10.dp
                    )
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.width(this.maxWidth.div(2)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Miles", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        yearMetrics.totalDistance.getDistanceString(selectedUnitType!!),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text("Avg Pace", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        getAveragePaceString(
                            yearMetrics.totalDistance,
                            yearMetrics.totalTime,
                            selectedUnitType
                        ),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Column(
                    modifier = Modifier
                        .width(this.maxWidth.div(2))
                        .align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Elevation", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${yearMetrics.totalElevation.getElevationString(selectedUnitType!!)}",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text("Avg Distance/Run", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        yearMetrics.totalDistance.div(yearMetrics.count)
                            .getDistanceString(selectedUnitType!!),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

            }
        })
}