package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName


data class Cursors(

    @SerializedName("after") var after: String? = null,
    @SerializedName("before") var before: String? = null

)