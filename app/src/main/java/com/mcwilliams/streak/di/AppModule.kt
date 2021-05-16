package com.mcwilliams.streak.di

import android.content.Context
import com.mcwilliams.streak.ui.dashboard.StravaDashboardRepository
import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.api.AthleteApi
import com.mcwilliams.streak.ui.settings.SettingsRepo
import com.mcwilliams.streak.ui.settings.SettingsRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [StravaNetworkModule::class], )
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
        athleteApi: AthleteApi
    ): SettingsRepo =
        SettingsRepoImpl(settingsRepoImpl, athleteApi)
}