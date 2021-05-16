package com.mcwilliams.streak.strava.model.activitydetail

data class SplitsMetric(
    val average_speed: Float,
    val distance: Float,
    val elapsed_time: Int,
    val elevation_difference: Double,
    val moving_time: Int,
    val pace_zone: Int,
    val split: Int,
    val average_heartrate: Float
)