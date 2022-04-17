package com.mcwilliams.streak

import android.app.Application
import android.content.SharedPreferences
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StreakApplication() : Application(), Configuration.Provider {

    //    @Inject
//    lateinit var workerFactory: WorkerFactory
    @Inject
    lateinit var workerConfiguration: Configuration

    // Setup custom configuration for WorkManager with a DelegatingWorkerFactory
    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }

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