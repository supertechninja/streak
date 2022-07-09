package com.mcwilliams.streak.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.getDistanceMiles
import com.mcwilliams.streak.ui.utils.round
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoalsContent(
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
    viewModel: StravaDashboardViewModel
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Goals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Annual Goal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
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
                    label = {
                        Text(text = "Total Miles")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        placeholderColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = .8f),
                        backgroundColor = MaterialTheme.colorScheme.onPrimary
                    )
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
                        }$distanceLabel",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}