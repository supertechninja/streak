package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName


data class ExternalIds(

    @SerializedName("isrc") var isrc: String? = null

)