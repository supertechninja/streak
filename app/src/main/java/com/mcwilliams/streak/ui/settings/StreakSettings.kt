package com.mcwilliams.streak.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel

@Composable
fun StreakSettingsView(paddingValues: PaddingValues, viewModel: StravaDashboardViewModel) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(color = Color(0xFF01374D)),
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
                    .padding(paddingValues = paddingValues)
                    .background(color = Color(0xFF01374D))
                    .fillMaxSize()
            ) {
                var showDropDownMenu by remember { mutableStateOf(false) }
                val selectedActivityType by viewModel.activityType.observeAsState()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
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
                                    color = Color(0xFF036e9a),
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

                                    if (selectedActivityType!!.name == activityType.name) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Currently Selected"
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Units",
                            style = MaterialTheme.typography.h6
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = Color(0xFF036e9a),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
//                                        viewModel.updateSelectedActivity(activityType = activityType)
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(40.dp),

                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Imperial")

//                                if (selectedActivityType!!.name == activityType.name) {
//                                    Icon(
//                                        imageVector = Icons.Default.Check,
//                                        contentDescription = "Currently Selected"
//                                    )
//                                }
                            }

                            Row(
                                modifier = Modifier
                                    .clickable {
//                                        viewModel.updateSelectedActivity(activityType = activityType)
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(40.dp),

                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Metric")

//                                if (selectedActivityType!!.name == activityType.name) {
//                                    Icon(
//                                        imageVector = Icons.Default.Check,
//                                        contentDescription = "Currently Selected"
//                                    )
//                                }
                            }
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(onClick = { viewModel.logout() }) {
                        Text(text = "Log out")
                    }
                }
            }
        })
}