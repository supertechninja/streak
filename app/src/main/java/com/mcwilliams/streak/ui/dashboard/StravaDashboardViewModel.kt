package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.StravaSessionRepository
import com.mcwilliams.streak.strava.model.activitydetail.StravaActivityDetail
import com.mcwilliams.streak.ui.dashboard.data.CalendarActivities
import com.mcwilliams.streak.ui.dashboard.data.CalendarData
import com.mcwilliams.streak.ui.settings.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class StravaDashboardViewModel @Inject constructor(
    private val stravaDashboardRepository: StravaDashboardRepository,
    private val stravaSessionRepository: StravaSessionRepository,
    private val settingsRepo: SettingsRepo,
) : ViewModel() {

    val widgetStatus = mutableStateOf(false)

    private var _isLoggedInStrava: MutableLiveData<Boolean> = MutableLiveData(null)
    var isLoggedInStrava: LiveData<Boolean> = _isLoggedInStrava

    private val _activityUiState: MutableStateFlow<ActivityUiState> =
        MutableStateFlow(ActivityUiState.Loading)
    val activityUiState: StateFlow<ActivityUiState> = _activityUiState.asStateFlow()

    var _activityType: MutableLiveData<ActivityType> = MutableLiveData()
    var activityType: LiveData<ActivityType> = _activityType

    var _unitType: MutableLiveData<UnitType> = MutableLiveData()
    var unitType: LiveData<UnitType> = _unitType

    var _measureType: MutableLiveData<MeasureType> = MutableLiveData()
    var measureType: LiveData<MeasureType> = _measureType

    val calendarData = CalendarData()

    var _weeklyActivityDetails: MutableLiveData<List<StravaActivityDetail>> = MutableLiveData()
    var weeklyActivityDetails: LiveData<List<StravaActivityDetail>> = _weeklyActivityDetails

    init {
        _isLoggedInStrava.postValue(stravaSessionRepository.isLoggedIn())
    }

    fun fetchData() {
        _activityUiState.tryEmit(ActivityUiState.Loading)

        _activityType.postValue(stravaDashboardRepository.getPreferredActivity())

        _unitType.postValue(stravaDashboardRepository.getPreferredUnitType())

        viewModelScope.launch {
            stravaDashboardRepository.loadActivities(
                after = null,
                before = calendarData.currentYear.first,
            ).catch { exception ->
                Log.d("TAG", "fetchData: $exception")
                val errorCode = (exception as HttpException).code()

                val errorMessage = if (errorCode in 400..499) {
                    "Error! Force Refresh"
                } else {
                    "Have issues connecting to Strava"
                }

                _activityUiState.tryEmit(ActivityUiState.Error(errorMessage))
            }.collect { currentYearActivities ->
                _activityUiState.tryEmit(ActivityUiState.DataLoaded(currentYearActivities))

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
        _activityType.postValue(stravaDashboardRepository.getPreferredActivity())
    }

    fun updateSelectedUnit(unitType: UnitType) {
        stravaDashboardRepository.savePreferredUnits(unitType = unitType)
        _unitType.postValue(stravaDashboardRepository.getPreferredUnitType())
    }

    fun updateMeasureType(measureType: MeasureType) {
        stravaDashboardRepository.saveMeasureType(measureType = measureType)
        _measureType.postValue(stravaDashboardRepository.getPreferredMeasureType())
    }

    fun saveWeeklyStats(weeklyDistance: String, weeklyElevation: String) {
        stravaDashboardRepository.saveWeeklyDistance(weeklyDistance, weeklyElevation)
    }

    fun loadWeekActivityDetails(weeklyActivityIds : List<String>) {
        Log.d("TAG", "loadWeekActivityDetails: $weeklyActivityIds")
        viewModelScope.launch {
            stravaDashboardRepository.loadActivityDetails(weeklyActivityIds)
                .collectLatest {
                    _weeklyActivityDetails.postValue(it.map { it.second })
                }
        }
    }
}

enum class ActivityType { Run, Swim, Bike, All }
enum class UnitType { Imperial, Metric }
enum class MeasureType { Absolute, Relative }

sealed class ActivityUiState {
    object Loading : ActivityUiState()
    class DataLoaded(val calendarActivities: CalendarActivities) : ActivityUiState()
    class Error(val errorMessage: String) : ActivityUiState()
}