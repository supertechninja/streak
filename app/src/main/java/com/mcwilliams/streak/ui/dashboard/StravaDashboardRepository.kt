package com.mcwilliams.streak.ui.dashboard

import android.content.Context
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaDashboardRepository @Inject constructor(
    val context: Context,
    private val activitiesApi: ActivitiesApi
) {

    //Cache in memory the strava workouts
    private lateinit var listOfStravaWorkouts: List<ActivitesItem>

    fun getStravaActivitiesAfter(after: Int): Observable<List<ActivitesItem>> =
        activitiesApi.getAthleteActivitiesAfter(after)
//            .map { mapStravaWorkouts(it) }

    fun getStravaActivitiesBeforeAndAfter(
        before: Int,
        after: Int
    ): Observable<List<ActivitesItem>> =
        activitiesApi.getAthleteActivitiesBeforeAndAfter(before = before, after = after)

}