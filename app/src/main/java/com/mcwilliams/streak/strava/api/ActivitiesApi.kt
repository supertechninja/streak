package com.mcwilliams.streak.strava.api

import androidx.annotation.Keep
import com.mcwilliams.streak.strava.model.activites.ActivitesItem
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface ActivitiesApi {

    @GET("athlete/activities")
    suspend fun getAthleteActivitiesAfter(
        @Query("after") after: Int,
        @Query("per_page") count: Int = 200,
        @Query("page") page: Int = 1
    ): List<ActivitesItem>

    @GET("athlete/activities")
    suspend fun getAthleteActivitiesBeforeAndAfter(
        @Query("before") before: Int?,
        @Query("after") after: Int,
        @Query("per_page") count: Int = 200,
        @Query("page") page: Int = 1
    ): List<ActivitesItem>

}