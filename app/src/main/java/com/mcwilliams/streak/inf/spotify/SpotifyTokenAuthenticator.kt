package com.mcwilliams.streak.inf.spotify

import android.util.Log
import androidx.annotation.Keep
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

@Keep
class SpotifyTokenAuthenticator @Inject constructor(val spotifySessionRepository: SpotifySessionRepository) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // This is a synchronous call
        val updatedToken = getNewToken()

        val newRequest = response.request().newBuilder()
            .removeHeader("Authorization")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $updatedToken")
            .build()

        Log.d("TAG", "authenticate: ${newRequest.headers().names().toString()}")

        return newRequest
    }

    private fun getNewToken(): String {
        return runBlocking {
            return@runBlocking spotifySessionRepository.refreshToken()
        }
    }
}