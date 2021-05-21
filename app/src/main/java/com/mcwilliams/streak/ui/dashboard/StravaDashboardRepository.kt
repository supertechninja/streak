package com.mcwilliams.streak.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaDashboardRepository @Inject constructor(
    val context: Context,
    private val activitiesApi: ActivitiesApi
) {

    //Cache in memory the strava workouts
    private lateinit var listOfStravaWorkouts: List<ActivitesItem>

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    fun getStravaActivitiesAfter(after: Int): Flow<List<ActivitesItem>> =
        flow {
            emit(activitiesApi.getAthleteActivitiesAfter(after))
        }

    fun getStravaActivitiesBeforeAndAfter(
        before: Int,
        after: Int
    ): Flow<List<ActivitesItem>> = flow {
        emit(activitiesApi.getAthleteActivitiesBeforeAndAfter(before = before, after = after))
    }

    fun savePreferredActivity(activityType: ActivityType) {
        preferences.edit().putString(activityTypeKey, activityType.name).apply()
    }

    fun getPreferredActivity() = preferences.getString(activityTypeKey, ActivityType.Run.name)?.let {
        ActivityType.valueOf(
            it
        )
    }

    fun savePreferredUnits(unitType: UnitType) {
        preferences.edit().putString(unitTypeKey, unitType.name).apply()
    }

    fun getPreferredUnitType() = preferences.getString(unitTypeKey, UnitType.Imperial.name)?.let {
        UnitType.valueOf(
            it
        )
    }

    companion object {
        const val activityTypeKey: String = "activityType"
        const val unitTypeKey: String = "unitType"
    }
}