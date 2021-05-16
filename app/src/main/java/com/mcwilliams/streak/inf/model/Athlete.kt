package com.mcwilliams.streak.inf.model

data class Athlete(
    val badge_type_id: Int,
    val city: String,
    val country: Any,
    val created_at: String,
    val firstname: String,
    val follower: Any,
    val friend: Any,
    val id: Int,
    val lastname: String,
    val premium: Boolean,
    val profile: String,
    val profile_medium: String,
    val resource_state: Int,
    val sex: String,
    val state: String,
    val summit: Boolean,
    val updated_at: String,
    val username: String
)