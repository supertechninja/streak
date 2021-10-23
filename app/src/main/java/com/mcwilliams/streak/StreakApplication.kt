package com.mcwilliams.streak

import android.app.Application
import android.content.SharedPreferences
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StreakApplication() : Application() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this);

    }

    companion object {
        var isUserLoggedIn = false
    }

}