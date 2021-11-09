package com.mcwilliams.streak.ui.bottomnavigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.mcwilliams.streak.NavigationDestination

@Composable
fun BottomTab(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSecondary
        else Color.White.copy(.8f),
        animationSpec = tween(300)
    )

    val interactionSource = remember { MutableInteractionSource() }
    val ripple =
        rememberRipple(bounded = false, color = MaterialTheme.colorScheme.tertiary.copy(.8f))

    Column(
        modifier = modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = true,
            role = Role.Tab,
            interactionSource = interactionSource,
            indication = ripple
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.padding(
                horizontal = 24.dp, vertical = 4.dp
            ), tint = iconColor
        )

        Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
            color = if(selected) MaterialTheme.colorScheme.secondary.copy(.8f) else Color.LightGray
        )
    }
}

@Composable
fun BottomNavEffect(
    selectedTab: Int,
    updateSelectedTab: (Int) -> Unit,
    navController: NavHostController
) {
    BoxWithConstraints(
        modifier =
        Modifier
            .height(80.dp)
            .background(
                color = MaterialTheme.colorScheme.onSecondary
            ),
    ) {
        val bottomNavTabWidth = maxWidth / 3
        val tabModifier = Modifier.width(bottomNavTabWidth)

        //dp to offset the pill on the selected tab
        val offset by animateDpAsState(
            targetValue = ((bottomNavTabWidth * selectedTab) - ((bottomNavTabWidth / 2) + 30.dp)),
            animationSpec = tween(300)
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(width = 60.dp, height = 32.dp)
                .offset(x = offset, y = 12.dp)
        ) { }

        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            BottomTab(
                selected = selectedTab == 1,
                label = "Dashboard",
                icon = Icons.Default.Home,
                modifier = tabModifier
            ) {
                updateSelectedTab(1)
                navController.navigate(NavigationDestination.StravaDashboard.destination) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
            BottomTab(
                selected = selectedTab == 2,
                label = "Goals",
                icon = Icons.Default.Search,
                modifier = tabModifier
            ) {
                updateSelectedTab(2)
                navController.navigate(NavigationDestination.Goals.destination) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
            BottomTab(
                selected = selectedTab == 3,
                label = "Settings",
                icon = Icons.Default.Settings,
                modifier = tabModifier
            ) {
                updateSelectedTab(3)
                navController.navigate(NavigationDestination.StreakSettings.destination) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        }
    }
}