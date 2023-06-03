package com.mcwilliams.streak.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import com.mcwilliams.streak.R
import com.mcwilliams.streak.inf.StravaSessionRepository
import com.mcwilliams.streak.inf.model.Athlete
import com.mcwilliams.streak.strava.api.AthleteApi
import com.mcwilliams.streak.strava.model.profile.AthleteStats
import com.mcwilliams.streak.strava.model.profile.StravaAthlete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val sessionRepo: StravaSessionRepository,
    private val athleteApi: AthleteApi,
    val context: Context,
) : SettingsRepo, OnSharedPreferenceChangeListener {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    private val _widgetStatus = MutableSharedFlow<Boolean>(replay = 0)
    override val widgetStatus: SharedFlow<Boolean> = _widgetStatus

    fun refreshPrefs(): Boolean {
        return preferences.getBoolean("widgetEnable", false)
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        _widgetStatus.tryEmit(refreshPrefs())
    }

    override suspend fun authAthlete(code: String) {
        withContext(Dispatchers.IO) {
            sessionRepo.getFirstTokens(code)
        }
    }

    override suspend fun fetchAthlete(): StravaAthlete? = withContext(Dispatchers.IO) {
        val request = athleteApi.getAthlete()
        request
    }

    override suspend fun fetchAthleteStats(id: String): AthleteStats? =
        withContext(Dispatchers.IO) {
            athleteApi.getAthleteStats(id)
        }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        if (key == "widgetEnabled") {
            val widgetEnabled = preferences?.getBoolean("widgetEnable", false)
            widgetEnabled?.let {
                if (it) {
                    Log.d("TAG", "onSharedPreferenceChanged: $it")
                    _widgetStatus.tryEmit(refreshPrefs())
                }
            }
        }
    }
}