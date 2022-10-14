package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName


data class Artists(

    @SerializedName("external_urls") var externalUrls: ExternalUrls? = ExternalUrls(),
    @SerializedName("href") var href: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("uri") var uri: String? = null

)