package com.mcwilliams.streak.strava.api

import com.mcwilliams.streak.strava.model.profile.AthleteStats
import com.mcwilliams.streak.strava.model.profile.StravaAthlete
import retrofit2.http.GET
import retrofit2.http.Path

interface AthleteApi {
    @GET("athlete")
    suspend fun getAthlete(): StravaAthlete

    @GET("athletes/{id}/stats")
    suspend fun getAthleteStats(@Path("id") id: String): AthleteStats

}