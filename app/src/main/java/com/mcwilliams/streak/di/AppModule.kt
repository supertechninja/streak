package com.mcwilliams.streak.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import com.google.android.datatransport.runtime.dagger.multibindings.IntoMap
import com.mcwilliams.streak.ui.dashboard.StravaDashboardRepository
import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.api.AthleteApi
import com.mcwilliams.streak.ui.settings.SettingsRepo
import com.mcwilliams.streak.ui.settings.SettingsRepoImpl
import com.mcwilliams.streak.ui.widget.RefreshWidgetWorker
import com.mcwilliams.streak.ui.widget.RefreshWidgetWorkerFactory
import com.mcwilliams.streak.ui.widget.WidgetWorkerFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.reflect.KClass

@InstallIn(SingletonComponent::class)
@Module(includes = [StravaNetworkModule::class] )
class AppModule {

    @Provides
    @Singleton
    fun provideDashboardRepository(
        @ApplicationContext context: Context,
        activitiesApi: ActivitiesApi
    ): StravaDashboardRepository =
        StravaDashboardRepository(
            context,
            activitiesApi
        )

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsRepoImpl: SessionRepository,
        athleteApi: AthleteApi,
        @ApplicationContext context: Context,
    ): SettingsRepo =
        SettingsRepoImpl(settingsRepoImpl, athleteApi, context)

    @Singleton
    @Provides
    fun provideWorkManagerConfiguration(
        widgetWorkerFactory: WidgetWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(widgetWorkerFactory)
            .build()
    }
}