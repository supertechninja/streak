package com.mcwilliams.streak.ui.widget

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class WeeklyStatsWidget : GlanceAppWidget() {
    var glanceId: GlanceId? = null
    var weeklyMiles by mutableStateOf<String?>(null)
    var weeklyElevation by mutableStateOf<String?>(null)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
//        glanceId = LocalGlanceId.current
//
//        val localContext = LocalContext.current
//
//        Material3WidgetTheme(context = localContext) {
//            Column(
//                modifier = GlanceModifier
//                    .fillMaxSize()
//                    .background(Color.White, MaterialTheme.colorScheme.primaryContainer)
//                    .padding(8.dp)
//                    .clickable(actionStartActivity(activity = MainActivity::class.java)),
//                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
//                verticalAlignment = Alignment.Vertical.CenterVertically
//            ) {
//                Text(
//                    text = "Streak",
//                    style = TextStyle(
//                        color = ColorProvider(MaterialTheme.colorScheme.primary),
//                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = GlanceModifier.padding(bottom = 4.dp)
//                )
//                Text(
//                    text = "Weekly Stats",
//                    style = TextStyle(
//                        color = ColorProvider(MaterialTheme.colorScheme.primary),
//                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
//                    ),
//                    modifier = GlanceModifier.padding(bottom = 12.dp)
//                )
//
//                Row(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        provider = ImageProvider(R.drawable.ic_ruler),
//                        contentDescription = "Distance",
//                        modifier = GlanceModifier.padding(end = 16.dp)
//                    )
//
//                    Text(
//                        weeklyMiles ?: "", style = TextStyle(
//                            color = ColorProvider(MaterialTheme.colorScheme.primary),
//                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
//                        )
//                    )
//                }
//
//                Row(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        provider = ImageProvider(R.drawable.ic_up_right),
//                        contentDescription = "Elevation",
//                        modifier = GlanceModifier.padding(end = 16.dp)
//                    )
//                    Text(
//                        weeklyElevation ?: "", style = TextStyle(
//                            color = ColorProvider(MaterialTheme.colorScheme.primary),
//                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
//                        )
//                    )
//                }
//            }
//        }
    }
}