package com.mcwilliams.streak.strava.model.activitydetail

data class Gear(
    val distance: Int,
    val id: String,
    val name: String,
    val primary: Boolean,
    val resource_state: Int
)