package com.mcwilliams.streak.inf.spotify

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.mcwilliams.streak.inf.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

@Keep
interface SpotifySessionApi {
    @POST("/api/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("code") code: String?,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUri: String
    ) : TokenResponse
}


@Keep
interface SpotifyApis {
    @GET("/v1/me/player/recently-played")
    suspend fun getRecentlyPlayedSongs() : Response<JsonObject>
}