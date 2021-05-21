package com.mcwilliams.streak.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.SessionRepository
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import com.mcwilliams.streak.ui.settings.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    var currentYearEpoch = 0
    var currentYear = ""
    var _currentYearActivites: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var currentYearActivites: LiveData<List<ActivitesItem>> =
        _currentYearActivites

    var prevYearEpoch = 0
    var prevYear = ""
    var _prevYearActivites: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var prevYearActivites: LiveData<List<ActivitesItem>> =
        _prevYearActivites

    var prevPrevYearEpoch = 0
    var prevPrevYear = ""
    var _prevPrevYearActivites: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var prevPrevYearActivites: LiveData<List<ActivitesItem>> =
        _prevPrevYearActivites

    var _activityType: MutableLiveData<ActivityType> = MutableLiveData(ActivityType.Run)
    var activityType: LiveData<ActivityType> = _activityType

    var _error: MutableLiveData<String> =
        MutableLiveData()
    var error: LiveData<String> =
        _error

    var _lastTwoMonthsActivities: MutableLiveData<List<ActivitesItem>> =
        MutableLiveData()
    var lastTwoMonthsActivities: LiveData<List<ActivitesItem>> =
        _lastTwoMonthsActivities

    init {
        _isLoggedIn.postValue(sessionRepository.isLoggedIn())

        val currentMonthInt = LocalDate.now().monthValue

        currentMonthEpoch = getEpoch(2021, currentMonthInt - 1, 1).first
        currentMonth = getEpoch(2021, currentMonthInt - 1, 1).second

        previousMonthEpoch = getEpoch(2021, currentMonthInt - 2, 1).first
        previousMonth = getEpoch(2021, currentMonthInt - 2, 1).second

        previousPreviousMonthEpoch = getEpoch(2021, currentMonthInt - 3, 1).first
        previousPreviousMonth = getEpoch(2021, currentMonthInt - 3, 1).second

        currentYearEpoch = getEpoch(2021, 0, 1).first
        prevYearEpoch = getEpoch(2020, 0, 1).first
        prevPrevYearEpoch = getEpoch(2019, 0, 1).first

    }

    fun fetchData() {
        viewModelScope.launch {
            stravaDashboardRepository.getStravaActivitiesAfter(currentMonthEpoch)
                .catch { exception ->
                    val errorCode = (exception as HttpException).code()
                    if (errorCode in 400..499) {
                        _error.postValue("Error! Force Refresh")
                    } else {
                        _error.postValue("Have issues connecting to Strava")
                    }
                }
                .collect {
                    _currentMonthActivites.postValue(it)
                    _error.postValue(null)
                }

            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
                after = previousMonthEpoch,
                before = currentMonthEpoch
            ).catch { exception ->
                val errorCode = (exception as HttpException).code()
                if (errorCode in 400..499) {
                    _error.postValue("Error! Force Refresh")
                } else {
                    _error.postValue("Have issues connecting to Strava")
                }
            }
                .collect {
                    _error.postValue(null)
                    _previousMonthActivities.postValue(it)
                }

            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
                after = previousPreviousMonthEpoch,
                before = previousMonthEpoch
            ).catch { exception ->
                val errorCode = (exception as HttpException).code()
                if (errorCode in 400..499) {
                    _error.postValue("Error! Force Refresh")
                } else {
                    _error.postValue("Have issues connecting to Strava")
                }
            }
                .collect {
                    _error.postValue(null)
                    _previousPreviousMonthActivities.postValue(it)
                }

            val combinedList = currentMonthActivites.value!!.toMutableList()
            combinedList.plus(previousMonthActivities.value?.toMutableList())
            _lastTwoMonthsActivities.postValue(combinedList)
        }


//        currentMonthActivites =
//            stravaDashboardRepository.getStravaActivitiesAfter(currentMonthEpoch)
//                .toLiveData(rootDisposable) { it }
//
//        previousMonthActivities =
//            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
//                after = previousMonthEpoch,
//                before = currentMonthEpoch
//            ).toLiveData(rootDisposable) { it }
//
//        previousPreviousMonthActivities =
//            stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
//                after = previousPreviousMonthEpoch,
//                before = previousMonthEpoch
//            ).toLiveData(rootDisposable) { it }

//        currentYearActivites = stravaDashboardRepository.getStravaActivitiesAfter(currentYearEpoch)
//            .toLiveData(rootDisposable) { it }
//
//        prevYearActivites = stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
//            after = prevYearEpoch,
//            before = currentYearEpoch
//        ).toLiveData(rootDisposable) { it }
//
//        prevPrevYearActivites = stravaDashboardRepository.getStravaActivitiesBeforeAndAfter(
//            after = prevPrevYearEpoch,
//            before = prevYearEpoch
//        ).toLiveData(rootDisposable) { it }
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

fun getEpoch(year: Int, month: Int, day: Int): Pair<Int, String> {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return Pair(
        calendar.toInstant().epochSecond.toInt(),
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    )
}