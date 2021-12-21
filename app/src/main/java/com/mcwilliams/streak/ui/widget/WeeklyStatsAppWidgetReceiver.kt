package com.mcwilliams.streak.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mcwilliams.streak.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class WeeklyStatsAppWidgetReceiver() : GlanceAppWidgetReceiver() {
    private val coroutineScope = MainScope()

    private val weeklyStatsWidget = WeeklyStatsWidget()
    override val glanceAppWidget: GlanceAppWidget = weeklyStatsWidget

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val preferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        coroutineScope.launch {
            val weeklyStatsString = preferences.getString("weeklyDistance", "")
            val weeklyElevationString = preferences.getString("weeklyElevation", "")

            weeklyStatsWidget.weeklyMiles = weeklyStatsString
            weeklyStatsWidget.weeklyElevation = weeklyElevationString
            weeklyStatsWidget.glanceId?.let { glanceId ->
                coroutineScope.launch {
                    weeklyStatsWidget.update(context, glanceId)
                }

            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}