package com.mcwilliams.streak.ui.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceId
import androidx.glance.appwidget.LocalGlanceId
import androidx.glance.appwidget.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mcwilliams.streak.ui.theme.Material3Theme

class WeeklyStatsWidget : GlanceAppWidget() {
    var glanceId: GlanceId? = null
    var weeklyMiles by mutableStateOf<String?>(null)

    @Composable
    override fun Content() {
        glanceId = LocalGlanceId.current

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White, MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "Streak",
                style = TextStyle(
                    color = ColorProvider(MaterialTheme.colorScheme.primary),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 20.dp)
            )

            Text(
                text = "Miles",
                style = TextStyle(
                    color = ColorProvider(MaterialTheme.colorScheme.primary),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            )
            Text(
                weeklyMiles ?: "", style = TextStyle(
                    color = ColorProvider(MaterialTheme.colorScheme.primary),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            )
        }
    }
}