package com.mcwilliams.streak.strava.model.activites

import androidx.annotation.Keep

@Keep
data class Athlete(
    val id: Number,
    val resource_state: Int
)