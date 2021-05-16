package com.mcwilliams.streak.strava.model.profile

data class ActivityTotal(
    val achievement_count: Int,
    val count: Int,
    val distance: Double,
    val elapsed_time: Int,
    val elevation_gain: Double,
    val moving_time: Int
)