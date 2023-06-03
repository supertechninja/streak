package com.mcwilliams.streak.inf.spotify

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.mcwilliams.streak.inf.model.TokenResponse
import com.mcwilliams.streak.inf.spotify.model.RecentlyPlayedSongs
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Keep
interface SpotifySessionApi {
    @POST("/api/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("code") code: String?,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUri: String
    ): TokenResponse

    @POST("/api/token")
    @FormUrlEncoded
    suspend fun getTokenUsingRefresh(
        @Field("refresh_token") refreshToken: String?,
        @Field("grant_type") grantType: String = "refresh_token",
    ): TokenResponse
}


@Keep
interface SpotifyApis {
    @GET("/v1/me/player/recently-played")
    suspend fun getRecentlyPlayedSongs(
        @Query("before") before: Long,
    ): Response<RecentlyPlayedSongs>
}