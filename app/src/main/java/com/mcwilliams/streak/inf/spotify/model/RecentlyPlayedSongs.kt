package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName


data class RecentlyPlayedSongs(
    @SerializedName("items") var items: ArrayList<Items> = arrayListOf(),
    @SerializedName("next") var next: String? = null,
    @SerializedName("cursors") var cursors: Cursors? = Cursors(),
    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("href") var href: String? = null

)