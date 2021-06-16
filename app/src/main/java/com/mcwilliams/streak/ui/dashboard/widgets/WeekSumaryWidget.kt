package com.mcwilliams.streak.ui.dashboard.widgets

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.DashboardStat
import com.mcwilliams.streak.ui.dashboard.StreakWidgetCard
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.getElevationString
import com.mcwilliams.streak.ui.utils.getTimeStringHoursAndMinutes
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekSummaryWidget(
    selectedActivityType: ActivityType?,
    currentWeek: MutableList<Pair<Int, Int>>,
    selectedUnitType: UnitType?,
    weeklySummaryMetrics: SummaryMetrics,
    dayOfWeekWithDistance: MutableMap<Int, Int>,
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

                Row() {
                    Column(
                        modifier = Modifier.width(width = width),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row() {
                            Text(
                                text = selectedActivityType?.name!!,
                                color = Color(0xFFFFA500),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.body2
                            )
                            if (currentWeek.isNotEmpty()) {
                                val text =
                                    " | ${
                                        Month.of(currentWeek[0].first).getDisplayName(
                                            TextStyle.SHORT_STANDALONE,
                                            Locale.getDefault()
                                        )
                                    } ${currentWeek[0].second}-" +
                                            "${
                                                Month.of(currentWeek.last().first).getDisplayName(
                                                    TextStyle.SHORT_STANDALONE,
                                                    Locale.getDefault()
                                                )
                                            }  ${currentWeek.last().second}"
                                Text(
                                    text = text,
                                    color = MaterialTheme.colors.onSurface,
                                    style = MaterialTheme.typography.body2
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
                            stat = weeklySummaryMetrics.totalDistance.getDistanceString(selectedUnitType!!)
                        )

                        DashboardStat(
                            image = R.drawable.ic_clock_time,
                            stat = weeklySummaryMetrics.totalTime.getTimeStringHoursAndMinutes()
                        )

                        DashboardStat(
                            image = R.drawable.ic_up_right,
                            stat = "${weeklySummaryMetrics.totalElevation.getElevationString(selectedUnitType!!)}"
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(width = width)
                            .height(120.dp)
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (progress, days) = createRefs()
                            val coroutineScope = rememberCoroutineScope()

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
                                            var showValue by remember { mutableStateOf(false) }
                                            if (it.key == dateInWeek.second) {
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
                                                    if (showValue) {
                                                        Text(
                                                            text = "${it.value / 1609}",
                                                            style = MaterialTheme.typography.overline
                                                        )
                                                    }

                                                    Divider(
                                                        color = Color(
                                                            0xFFFFA500
                                                        ),
                                                        modifier = Modifier
                                                            .height(
                                                                progressHeight
                                                            )
                                                            .width(20.dp)
                                                            .clip(
                                                                RoundedCornerShape(
                                                                    50
                                                                )
                                                            )
                                                            .padding(
                                                                horizontal = 4.dp
                                                            )
                                                            .pointerInput(Unit) {
                                                                detectTapGestures(
                                                                    onPress = {
                                                                        coroutineScope.launch {
                                                                            showValue = true
                                                                            awaitRelease()
                                                                            showValue = false
                                                                        }
                                                                    },
                                                                    onDoubleTap = { /* Called on Double Tap */ },
                                                                    onLongPress = { /* Called on Long Press */ },
                                                                    onTap = { /* Called on Tap */ }
                                                                )

                                                            }
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
                                            color = MaterialTheme.colors.onSurface,
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