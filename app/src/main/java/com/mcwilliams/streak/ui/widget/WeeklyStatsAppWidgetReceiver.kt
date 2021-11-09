package com.mcwilliams.streak.ui.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mcwilliams.streak.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
            weeklyStatsWidget.weeklyMiles = weeklyStatsString
            weeklyStatsWidget.glanceId?.let {
                weeklyStatsWidget.update(context, it)
            }
        }
    }
}