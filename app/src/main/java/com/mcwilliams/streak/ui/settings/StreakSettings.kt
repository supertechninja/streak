package com.mcwilliams.streak.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.UnitType

@OptIn(ExperimentalMaterial3Api::class)
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
                    .background(color = MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = {
            Box() {

                Column(
                    modifier = Modifier
                        .padding(it)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .fillMaxHeight()
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
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                                        Text(
                                            activityType.name,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        selectedActivityType?.let {
                                            if (it.name == activityType.name) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Currently Selected",
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Units",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                                    Text("Imperial", color = MaterialTheme.colorScheme.onSurface)

                                    if (UnitType.Imperial.name == selectedUnitType?.name) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Currently Selected",
                                            tint = MaterialTheme.colorScheme.onSurface
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
                                    Text("Metric", color = MaterialTheme.colorScheme.onSurface)

                                    if (UnitType.Metric.name == selectedUnitType?.name) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Currently Selected",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }


                    val widgetStatus by viewModel.widgetStatus
                    if (widgetStatus) {
                        Log.d("TAG", "StreakSettingsView: TRUE")
                    }

                    Box(
                        modifier = Modifier
                            .padding(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        androidx.compose.material3.Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Log out")
                        }
//                    Button(
//                        onClick = { viewModel.logout() },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
//                    ) {
//                        Text(text = "Log out", color = MaterialTheme.colorScheme.onPrimary)
//                    }
                    }
                }

            }
        }
    )
}