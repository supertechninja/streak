package com.mcwilliams.streak.strava.model.activites

import androidx.annotation.Keep

@Keep
data class Map(
    val id: String,
    val resource_state: Int,
    val summary_polyline: String
)