package com.mcwilliams.streak.inf.model

import com.mcwilliams.streak.inf.model.Athlete

data class TokenResponse(
    val access_token: String,
    val athlete: Athlete?,
    val expires_at: Int,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String
)