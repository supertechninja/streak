package com.mcwilliams.streak.inf.spotify

import androidx.annotation.Keep

@Keep
data class SpotifyTokenResponse(
    val access_token: String,
    val refresh_token: String,
)