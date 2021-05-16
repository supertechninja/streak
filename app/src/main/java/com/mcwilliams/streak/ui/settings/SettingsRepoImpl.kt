package com.mcwilliams.streak.ui.settings

import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.inf.model.Athlete
import com.mcwilliams.streak.strava.api.AthleteApi
import com.mcwilliams.streak.strava.model.profile.AthleteStats
import com.mcwilliams.streak.strava.model.profile.StravaAthlete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val sessionRepo: SessionRepository,
    private val athleteApi: AthleteApi
) : SettingsRepo {

    override suspend fun authAthlete(code: String): Athlete? = withContext(Dispatchers.IO) {
        val request = sessionRepo.getFirstTokens(code).athlete
        request
    }

    override suspend fun fetchAthlete(): StravaAthlete? = withContext(Dispatchers.IO) {
        val request = athleteApi.getAthlete()
        request
    }

    override suspend fun fetchAthleteStats(id: String): AthleteStats? =
        withContext(Dispatchers.IO) {
            athleteApi.getAthleteStats(id)
        }

}