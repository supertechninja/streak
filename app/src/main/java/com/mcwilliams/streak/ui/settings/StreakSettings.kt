package com.mcwilliams.streak.ui.settings

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
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.theme.primaryColor
import kotlinx.coroutines.Job

@ExperimentalComposeUiApi
@Composable
fun StreakSettingsView(
    viewModel: StravaDashboardViewModel,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
    toggleBottomSheet: () -> Job
) {
    var weeklyGoal by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color.White.copy(alpha = .6f),
                modifier = Modifier
                    .width(50.dp)
                    .height(4.dp),
                shape = RoundedCornerShape(2.dp)
            ) {

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                toggleBottomSheet()
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Bottom Sheet")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                selectedUnitType?.let {
                    Text(
                        text = "Weekly Goal (${if (selectedUnitType == UnitType.Imperial) "mi)" else "km)"}",
                        style = MaterialTheme.typography.h6
                    )

                    OutlinedTextField(
                        value = weeklyGoal,
                        {
                            weeklyGoal = it
                            viewModel.setWeeklyGoal(weeklyGoal)
                        },
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                    )
                }

                Text(
                    text = "Activities",
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(
                            color = primaryColor,
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
                            Text(activityType.name)

                            selectedActivityType?.let {
                                if (it.name == activityType.name) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Currently Selected"
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
                            color = primaryColor,
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
                        Text("Imperial")

                        if (UnitType.Imperial.name == selectedUnitType?.name) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Currently Selected"
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
                        Text("Metric")

                        if (UnitType.Metric.name == selectedUnitType?.name) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Currently Selected"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

//                        Text(
//                            text = "Terms & Conditions",
//                            style = MaterialTheme.typography.h6
//                        )
//                        Spacer(modifier = Modifier.height(32.dp))
//
//                        Text(
//                            text = "Privacy Policy",
//                            style = MaterialTheme.typography.h6
//                        )
//                        Spacer(modifier = Modifier.height(32.dp))
//
//                        Text(
//                            text = "App Feedback",
//                            style = MaterialTheme.typography.h6
//                        )

            }
        }

        Box(
            modifier = Modifier
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(onClick = { viewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Log out", color = MaterialTheme.colors.onSurface)
            }
        }
    }
}
//            }
//        })
//}