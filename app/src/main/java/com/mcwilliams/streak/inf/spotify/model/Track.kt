package com.mcwilliams.streak.inf.spotify.model

import com.google.gson.annotations.SerializedName
import com.mcwilliams.streak.inf.spotify.model.Album
import com.mcwilliams.streak.inf.spotify.model.Artists
import com.mcwilliams.streak.inf.spotify.model.ExternalIds
import com.mcwilliams.streak.inf.spotify.model.ExternalUrls

data class Track(
    @SerializedName("album") var album: Album? = Album(),
    @SerializedName("artists") var artists: ArrayList<Artists> = arrayListOf(),
    @SerializedName("available_markets") var availableMarkets: ArrayList<String> = arrayListOf(),
    @SerializedName("disc_number") var discNumber: Int? = null,
    @SerializedName("duration_ms") var durationMs: Int? = null,
    @SerializedName("explicit") var explicit: Boolean? = null,
    @SerializedName("external_ids") var externalIds: ExternalIds? = ExternalIds(),
    @SerializedName("external_urls") var externalUrls: ExternalUrls? = ExternalUrls(),
    @SerializedName("href") var href: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("is_local") var isLocal: Boolean? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("popularity") var popularity: Int? = null,
    @SerializedName("preview_url") var previewUrl: String? = null,
    @SerializedName("track_number") var trackNumber: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("uri") var uri: String? = null
)