package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName


data class Images (

  @SerializedName("height" ) var height : Int?    = null,
  @SerializedName("url"    ) var url    : String? = null,
  @SerializedName("width"  ) var width  : Int?    = null

)