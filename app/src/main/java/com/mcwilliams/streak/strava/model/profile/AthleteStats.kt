package com.mcwilliams.streak.strava.model.profile

import com.mcwilliams.streak.strava.model.profile.ActivityTotal

data class AthleteStats(
    val all_ride_totals: ActivityTotal,
    val all_run_totals: ActivityTotal,
    val all_swim_totals: ActivityTotal,
    val biggest_climb_elevation_gain: Double,
    val biggest_ride_distance: Double,
    val recent_ride_totals: ActivityTotal,
    val recent_run_totals: ActivityTotal,
    val recent_swim_totals: ActivityTotal,
    val ytd_ride_totals: ActivityTotal,
    val ytd_run_totals: ActivityTotal,
    val ytd_swim_totals: ActivityTotal
)