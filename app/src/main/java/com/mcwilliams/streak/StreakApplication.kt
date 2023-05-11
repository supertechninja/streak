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

//        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())

    }

    companion object {
        var isUserLoggedIn = false
    }

}