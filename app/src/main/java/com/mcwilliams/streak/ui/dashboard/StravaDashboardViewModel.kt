package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.settings.SettingsRepo
import com.mcwilliams.streak.ui.settings.toLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StravaDashboardViewModel @Inject constructor(
    private val stravaDashboardRepository: StravaDashboardRepository,
    private val sessionRepository: SessionRepository,
    private val settingsRepo: SettingsRepo,
) : ViewModel() {

    val rootDisposable = CompositeDisposable()

    private var _isLoggedIn = MutableLiveData(false)
    var isLoggedIn: LiveData<Boolean> = _isLoggedIn

    var currentMonthEpoch = 0
    var currentMonth = ""
    var _currentMonthActivites: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var currentMonthActivites: LiveData<List<ActivitesItem>> =
        _currentMonthActivites

    var previousMonthEpoch = 0
    var previousMonth = ""
    var _previousMonthActivities: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var previousMonthActivities: LiveData<List<ActivitesItem>> =
        _previousMonthActivities


    var previousPreviousMonthEpoch = 0
    var previousPreviousMonth = ""
    var _previousPreviousMonthActivities: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var previousPreviousMonthActivities: LiveData<List<ActivitesItem>> =
        _previousPreviousMonthActivities

    var _activityType: MutableLiveData<ActivityType> = MutableLiveData(ActivityType.Run)
    var activityType: LiveData<ActivityType> = _activityType


    init {
        _isLoggedIn.postValue(sessionRepository.isLoggedIn())

        val currentMonthInt = LocalDate.now().monthValue
        val currentMonthCalendar: Calendar = Calendar.getInstance()
        currentMonthCalendar.set(2021, currentMonthInt - 1, 1)
        currentMonthEpoch = currentMonthCalendar.toInstant().epochSecond.toInt()
        currentMonth =
            currentMonthCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        Log.d("TAG", "Current: $currentMonthEpoch")

        val previousMonthCalendar: Calendar = Calendar.getInstance()
        previousMonthCalendar.set(2021, currentMonthInt - 2, 1)
        previousMonthEpoch = previousMonthCalendar.toInstant().epochSecond.toInt()
        previousMonth =
            previousMonthCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        Log.d("TAG", "Last Mon: $previousMonthEpoch")

        val previousPreviousMonthCalendar: Calendar = Calendar.getInstance()
        previousPreviousMonthCalendar.set(2021, currentMonthInt - 3, 1)
        previousPreviousMonthEpoch = previousPreviousMonthCalendar.toInstant().epochSecond.toInt()
        previousPreviousMonth = previousPreviousMonthCalendar.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale.getDefault()
        )
        Log.d("TAG", "Last 2 Mon: $previousPreviousMonthEpoch")

    }

    fun fetchData() {
        currentMonthActivites =
            stravaDashboardRepository.getStravaActivitiesAfter(currentMonthEpoch)
                .toLiveData(rootDisposable) { it }

        previousMonthActivities =
            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
                after = previousMonthEpoch,
                before = currentMonthEpoch
            )
                .toLiveData(rootDisposable) { it }

        previousPreviousMonthActivities =
            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
                after = previousPreviousMonthEpoch,
                before = previousMonthEpoch
            )
                .toLiveData(rootDisposable) { it }
    }

    fun loginAthlete(code: String) {
        viewModelScope.launch {
            settingsRepo.authAthlete(code)
            _isLoggedIn.postValue(sessionRepository.isLoggedIn())
        }
    }

    fun logout() {
        sessionRepository.logOff()
        _isLoggedIn.postValue(false)
    }

    fun updateSelectedActivity(activityType: ActivityType) {
        _activityType.postValue(activityType)
    }
}

fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atZone(zone)?.toInstant()?.toEpochMilli()?.toInt()

enum class ActivityType { Run, Swim, Bike, All }