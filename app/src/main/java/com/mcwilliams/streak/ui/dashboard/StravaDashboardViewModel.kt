package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.StravaSessionRepository
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.settings.SettingsRepo
import com.mcwilliams.streak.ui.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class StravaDashboardViewModel @Inject constructor(
    private val stravaDashboardRepository: StravaDashboardRepository,
    private val stravaSessionRepository: StravaSessionRepository,
    private val settingsRepo: SettingsRepo,
) : ViewModel() {

    var currentYearSummaryMetrics: SummaryMetrics? = null

    val widgetStatus = mutableStateOf(false)

    private var _isLoggedInStrava: MutableLiveData<Boolean> = MutableLiveData(null)
    var isLoggedInStrava: LiveData<Boolean> = _isLoggedInStrava

    private var _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(true)
    var isRefreshing: LiveData<Boolean> = _isRefreshing

    var _currentMonthActivites: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var currentMonthActivites: LiveData<List<ActivitiesItem>> =
        _currentMonthActivites

    var _previousMonthActivities: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var previousMonthActivities: LiveData<List<ActivitiesItem>> =
        _previousMonthActivities

    var _previousPreviousMonthActivities: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var previousPreviousMonthActivities: LiveData<List<ActivitiesItem>> =
        _previousPreviousMonthActivities

    var _currentYearActivites: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var currentYearActivites: LiveData<List<ActivitiesItem>> =
        _currentYearActivites

    var _prevYearActivites: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var prevYearActivites: LiveData<List<ActivitiesItem>> =
        _prevYearActivites

    var _prevPrevYearActivites: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var prevPrevYearActivites: LiveData<List<ActivitiesItem>> =
        _prevPrevYearActivites

    var _activityType: MutableLiveData<ActivityType> = MutableLiveData()
    var activityType: LiveData<ActivityType> = _activityType

    var _unitType: MutableLiveData<UnitType> = MutableLiveData()
    var unitType: LiveData<UnitType> = _unitType

    var _error: MutableLiveData<String> =
        MutableLiveData()
    var error: LiveData<String> =
        _error

    var _lastTwoMonthsActivities: MutableLiveData<List<ActivitiesItem>> =
        MutableLiveData()
    var lastTwoMonthsActivities: LiveData<List<ActivitiesItem>> =
        _lastTwoMonthsActivities

    val calendarData = CalendarData()

    init {
        _isLoggedInStrava.postValue(stravaSessionRepository.isLoggedIn())
    }

    fun fetchData() {
        _isRefreshing.postValue(true)

        _activityType.postValue(stravaDashboardRepository.getPreferredActivity())

        _unitType.postValue(stravaDashboardRepository.getPreferredUnitType())

        viewModelScope.launch {
            stravaDashboardRepository.loadActivities(
                after = null,
                before = calendarData.currentYear.first,
            ).catch { exception ->
                Log.e("ERROR", "fetchData: ${exception.message}")
                val errorCode = (exception as HttpException).code()
                if (errorCode in 400..499) {
                    _error.postValue("Error! Force Refresh")
                } else {
                    _error.postValue("Have issues connecting to Strava")
                }
            }.collect { currentYearActivities ->
                _currentMonthActivites.postValue(currentYearActivities.filter {
                    it.start_date.getDate().monthValue == calendarData.currentMonthInt
                            && it.start_date.getDate().year == 2022
                })

                _previousMonthActivities.postValue(currentYearActivities.filter {
                    if (calendarData.currentMonthInt == 1) {
                        it.start_date.getDate().monthValue == 12
                                && it.start_date.getDate().year == 2021
                    } else {
                        it.start_date.getDate().monthValue == calendarData.currentMonthInt - 1
                                && it.start_date.getDate().year == 2022
                    }
                })

                _previousPreviousMonthActivities.postValue(currentYearActivities.filter {
                    if (calendarData.currentMonthInt == 1) {
                        it.start_date.getDate().monthValue == 11
                                && it.start_date.getDate().year == 2021
                    } else {
                        it.start_date.getDate().monthValue == calendarData.currentMonthInt - 2
                                && it.start_date.getDate().year == 2022
                    }
                })

                _currentYearActivites.postValue(currentYearActivities.filter {
                    it.start_date.getDate().year == 2022
                })
                _prevYearActivites.postValue(currentYearActivities.filter {
                    it.start_date.getDate().year == 2021
                })
                _prevPrevYearActivites.postValue(currentYearActivities.filter {
                    it.start_date.getDate().year == 2020
                })

                _isRefreshing.postValue(false)

                val combinedList = currentMonthActivites.value?.toMutableList()
                combinedList?.plus(previousMonthActivities.value?.toMutableList())
                _lastTwoMonthsActivities.postValue(combinedList)

                stravaDashboardRepository.widgetStatus.collect {
                    widgetStatus.value = it
                }

            }
        }
    }

    fun loginAthlete(code: String) {
        viewModelScope.launch {
            settingsRepo.authAthlete(code)
            _isLoggedInStrava.postValue(stravaSessionRepository.isLoggedIn())
        }
    }

    fun logout() {
        stravaSessionRepository.logOff()
        _isLoggedInStrava.postValue(false)
    }

    fun updateSelectedActivity(activityType: ActivityType) {
        stravaDashboardRepository.savePreferredActivity(activityType)
        _activityType.postValue(stravaDashboardRepository.getPreferredActivity()!!)
    }

    fun updateSelectedUnit(unitType: UnitType) {
        stravaDashboardRepository.savePreferredUnits(unitType = unitType)
        _unitType.postValue(stravaDashboardRepository.getPreferredUnitType()!!)
    }

    fun saveWeeklyStats(weeklyDistance: String, weeklyElevation: String) {
        stravaDashboardRepository.saveWeeklyDistance(weeklyDistance, weeklyElevation)
    }
}

enum class ActivityType { Run, Swim, Bike, All }
enum class UnitType { Imperial, Metric }