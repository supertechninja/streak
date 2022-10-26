package com.mcwilliams.streak.ui.widget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mcwilliams.streak.MainActivity
import com.mcwilliams.streak.R
import com.mcwilliams.streak.ui.theme.Material3WidgetTheme

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class WeeklyStatsWidget : GlanceAppWidget() {
    var glanceId: GlanceId? = null
    var weeklyMiles by mutableStateOf<String?>(null)
    var weeklyElevation by mutableStateOf<String?>(null)

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    override fun Content() {
        glanceId = LocalGlanceId.current

        val localContext = LocalContext.current

        Material3WidgetTheme(context = localContext) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color.White, MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
                    .clickable(actionStartActivity(activity = MainActivity::class.java)),
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
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Weekly Stats",
                    style = TextStyle(
                        color = ColorProvider(MaterialTheme.colorScheme.primary),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    modifier = GlanceModifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_ruler),
                        contentDescription = "Distance",
                        modifier = GlanceModifier.padding(end = 16.dp)
                    )

                    Text(
                        weeklyMiles ?: "", style = TextStyle(
                            color = ColorProvider(MaterialTheme.colorScheme.primary),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                }

                Row(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_up_right),
                        contentDescription = "Elevation",
                        modifier = GlanceModifier.padding(end = 16.dp)
                    )
                    Text(
                        weeklyElevation ?: "", style = TextStyle(
                            color = ColorProvider(MaterialTheme.colorScheme.primary),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                }
            }
        }
    }
}