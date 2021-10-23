package com.mcwilliams.streak.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.SummaryMetrics
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.dashboard.widgets.DashboardType
import com.mcwilliams.streak.ui.theme.primaryColor
import com.mcwilliams.streak.ui.utils.getDistanceMiles
import com.mcwilliams.streak.ui.utils.getDistanceString
import com.mcwilliams.streak.ui.utils.round
import kotlinx.coroutines.Job
import java.time.Duration
import java.time.LocalDateTime

@ExperimentalComposeUiApi
@Composable
fun StreakSettingsView(
    viewModel: StravaDashboardViewModel,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
//            .background(color = Color(0xFF01374D))
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Activities",
                            style = MaterialTheme.typography.h6
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colors.onPrimary,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            ActivityType.values().forEach { activityType ->
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.updateSelectedActivity(activityType = activityType)
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(40.dp),

                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(activityType.name, color = MaterialTheme.colors.onSurface)

                                    selectedActivityType?.let {
                                        if (it.name == activityType.name) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Currently Selected",
                                                tint = MaterialTheme.colors.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Units",
                            style = MaterialTheme.typography.h6
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colors.onPrimary,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateSelectedUnit(UnitType.Imperial)
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(40.dp),

                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Imperial", color = MaterialTheme.colors.onSurface)

                                if (UnitType.Imperial.name == selectedUnitType?.name) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Currently Selected",
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateSelectedUnit(UnitType.Metric)
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(40.dp),

                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Metric", color = MaterialTheme.colors.onPrimary)

                                if (UnitType.Metric.name == selectedUnitType?.name) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Currently Selected",
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "2021 Goal",
                            style = MaterialTheme.typography.h6
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val keyboardContext = LocalSoftwareKeyboardController.current
                        var annualGoal by remember { mutableStateOf(TextFieldValue("")) }
                        TextField(
                            value = annualGoal,
                            onValueChange = { annualGoal = it },
                            placeholder = {
                                Text(text = "750")
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardContext?.hide()
                                }
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val today = LocalDateTime.now()
                        val endOfYear = LocalDateTime.parse("2022-01-01T00:00:00.0000")
                        val remainingDays = Duration.between(today, endOfYear).toDays()

                        val currentYearSummaryMetrics = viewModel.currentYearSummaryMetrics

                        if (currentYearSummaryMetrics?.totalDistance!! > 0f && annualGoal.text.isNotEmpty()) {
                            val miles = currentYearSummaryMetrics.totalDistance.getDistanceMiles(
                                selectedUnitType!!,
                            )

                            val distanceRemaining =
                                annualGoal.text.removeSurrounding("\"").toInt() - miles

                            val distanceLabel = when (selectedUnitType) {
                                UnitType.Imperial -> {
                                    " mi"
                                }
                                UnitType.Metric -> {
                                    " m"
                                }
                            }

                            Text(
                                "Miles Per Day: ${
                                    (distanceRemaining / remainingDays).round(2)
                                }$distanceLabel" + "\n${remainingDays / 7} / ${
                                    (distanceRemaining / (remainingDays / 7)).round(2)
                                }$distanceLabel"
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }

                Box(
                    modifier = Modifier
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(onClick = { viewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Log out", color = MaterialTheme.colors.onPrimary)
                    }
                }
            }
        }
    )
}