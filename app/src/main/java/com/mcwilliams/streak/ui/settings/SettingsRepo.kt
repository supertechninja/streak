package com.mcwilliams.streak.ui.settings

import com.mcwilliams.streak.inf.model.Athlete
import com.mcwilliams.streak.strava.model.profile.AthleteStats
import com.mcwilliams.streak.strava.model.profile.StravaAthlete
import kotlinx.coroutines.flow.SharedFlow

interface SettingsRepo {
    val widgetStatus: SharedFlow<Boolean>

    suspend fun authAthlete(code: String): Athlete?

    suspend fun fetchAthlete(): StravaAthlete?

    suspend fun fetchAthleteStats(id: String): AthleteStats?
}