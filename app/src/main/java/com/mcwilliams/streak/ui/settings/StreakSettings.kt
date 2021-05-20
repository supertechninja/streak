package com.mcwilliams.streak.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel

@Composable
fun StreakSettingsView(paddingValues: PaddingValues, viewModel: StravaDashboardViewModel) {
    Column(
        modifier = Modifier
            .padding(paddingValues = paddingValues)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        var showDropDownMenu by remember { mutableStateOf(false) }
        val selectedActivityType by viewModel.activityType.observeAsState()

        Row(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Streak Settings",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onSurface,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.height(30.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = "Activity Types: ", modifier = Modifier.padding(end = 16.dp))
            }
            Column(
                modifier = Modifier
                    .height(30.dp)
                    .clickable { showDropDownMenu = !showDropDownMenu },
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = selectedActivityType!!.name,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                DropdownMenu(
                    expanded = showDropDownMenu,
                    onDismissRequest = { showDropDownMenu = !showDropDownMenu }) {

                    ActivityType.values().forEach { activityType ->
                        DropdownMenuItem(onClick = {
                            viewModel.updateSelectedActivity(activityType = activityType)
                            showDropDownMenu = !showDropDownMenu
                        }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
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
}