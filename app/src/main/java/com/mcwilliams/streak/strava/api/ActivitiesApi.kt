package com.mcwilliams.streak.strava.api

import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import retrofit2.http.GET
import retrofit2.http.Query

interface ActivitiesApi {

    @GET("athlete/activities")
    suspend fun getAthleteActivitiesAfter(
        @Query("after") after: Int,
        @Query("per_page") count: Int = 200
    ): List<ActivitesItem>

    @GET("athlete/activities")
    suspend fun getAthleteActivitiesBeforeAndAfter(
        @Query("before") before: Int,
        @Query("after") after: Int,
        @Query("per_page") count: Int = 200
    ): List<ActivitesItem>

}