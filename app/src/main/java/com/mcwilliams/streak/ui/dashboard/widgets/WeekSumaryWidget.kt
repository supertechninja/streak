package com.mcwilliams.streak.ui.dashboard.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import java.time.DayOfWeek
import kotlin.math.roundToInt

@Composable
fun WeekSummaryWidget(
    monthlyWorkouts: List<ActivitesItem>,
    selectedActivityType: ActivityType?,
    currentWeek: MutableList<Int>,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xFF036e9a)
    ) {

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

                if (currentWeek.contains(date.dayOfMonth)) {
                    if (activitesItem.type == selectedActivityType!!.name) {
                        totalElevation += activitesItem.total_elevation_gain

                        totalTime += activitesItem.elapsed_time

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

            Row() {
                Column(
                    modifier = Modifier.width(width = width),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row() {
                        Text(
                            text = selectedActivityType?.name!!,
                            color = Color(0xFFFFA500),
                        )
                        if (currentWeek.isNotEmpty()) {
                            Text(
                                "  |  May ${
                                    currentWeek[0]
                                }-${
                                    currentWeek.last()
                                }",
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .width(80.dp)
                            .padding(vertical = 4.dp)
                            .height(1.dp),
                        color = MaterialTheme.colors.onSurface
                    )
                    DashboardStat(
                        image = R.drawable.ic_ruler,
                        stat = "${totalDistance.div(1609).roundToInt()} mi"
                    )

                    DashboardStat(
                        image = R.drawable.ic_clock_time,
                        stat = totalTime.getTimeStringHoursAndMinutes()
                    )

                    DashboardStat(
                        image = R.drawable.ic_up_right,
                        stat = "${totalElevation.getElevationString()}"
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
                                        if (it.key == dateInWeek) {
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
                                                    color = Color(
                                                        0xFFFFA500
                                                    ),
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
                                        color = MaterialTheme.colors.onSurface
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