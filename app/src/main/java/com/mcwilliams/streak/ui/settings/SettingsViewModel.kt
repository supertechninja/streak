package com.mcwilliams.streak.ui.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.strava.model.profile.AthleteStats
import com.mcwilliams.streak.strava.model.profile.StravaAthlete
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private var _detailedAthlete = MutableLiveData<StravaAthlete>()
    var detailedAthlete: LiveData<StravaAthlete> = _detailedAthlete

    //    private var _workoutHistory = MutableLiveData<Int>()
    lateinit var workoutHistory: LiveData<Int>

    private var _athleteStats = MutableLiveData<AthleteStats>()
    var athleteStats: LiveData<AthleteStats> = _athleteStats

    private var _errorMessage = MutableLiveData<String>()
    var errorMessage: LiveData<String> = _errorMessage

    private var _isLoggedIn = MutableLiveData(false)
    var isLoggedIn: LiveData<Boolean> = _isLoggedIn

    val widgetStatus = mutableStateOf(false)

    init {
        _isLoggedIn.postValue(sessionRepository.isLoggedIn())

        viewModelScope.launch{
            settingsRepo.widgetStatus.collect{
                widgetStatus.value = it
            }
        }
    }

    fun loginAthlete(code: String) {
        viewModelScope.launch {
            settingsRepo.authAthlete(code)
            _isLoggedIn.postValue(sessionRepository.isLoggedIn())
        }
    }

    fun loadDetailedAthlete() {
        viewModelScope.launch {
            val detailedAthlete = settingsRepo.fetchAthlete()
            _detailedAthlete.postValue(detailedAthlete!!)
            _athleteStats.postValue(settingsRepo.fetchAthleteStats(detailedAthlete.id.toString()))
        }
    }

    fun logOff() {
        sessionRepository.logOff()
        _isLoggedIn.postValue(false)
    }
}