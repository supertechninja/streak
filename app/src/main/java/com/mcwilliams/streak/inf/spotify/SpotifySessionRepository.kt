package com.mcwilliams.streak.inf.spotify

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.Keep
import com.mcwilliams.streak.R
import com.mcwilliams.streak.inf.ISessionRepository
import com.mcwilliams.streak.inf.StravaSessionRepository
import com.mcwilliams.streak.inf.model.GrantType
import com.mcwilliams.streak.inf.model.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Keep
class SpotifySessionRepository @Inject constructor(
    val context: Context,
    private val spotifySessionApi: SpotifySessionApi
) : ISessionRepository {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    override suspend fun getFirstTokens(code: String): TokenResponse {
        val firstToken: TokenResponse
        withContext(context = Dispatchers.IO) {
            firstToken = spotifySessionApi.getToken(
                code,
                grantType = "authorization_code",
                redirectUri = "https://www.streakapp.com/authorize/"
            )
            setAccessToken(firstToken.access_token)
            setRefreshToken(firstToken.refresh_token)
            firstToken
        }

        return firstToken
    }

    override suspend fun refreshToken(): String {
        return withContext(context = Dispatchers.IO) {
            Log.d("TAG", "refreshToken: refreshing token")
            val newTokens = spotifySessionApi.getTokenUsingRefresh(
                refreshToken = getRefreshToken()
            )

            setAccessToken(newTokens.access_token)
//            setRefreshToken(newTokens.refresh_token)
            newTokens.access_token
        }
    }

    override fun setAccessToken(accessToken: String) {
        with(preferences.edit()) {
            putString(ACCESS_TOKEN, accessToken)
            commit()
        }
    }

    override fun getAccessToken(): String {
        return preferences.getString(ACCESS_TOKEN, "") ?: ""
    }

    override fun setRefreshToken(refreshToken: String) {
        with(preferences.edit()) {
            putString(REFRESH_TOKEN, refreshToken)
            commit()
        }
    }

    override fun getRefreshToken(): String {
        return preferences.getString(REFRESH_TOKEN, "") ?: ""
    }

    override fun setExpiration(expiration: Int) {
        with(preferences.edit()) {
            putInt(EXPIRATION, expiration)
            commit()
        }
    }

    override fun getExpiration(): Int {
        return preferences.getInt(EXPIRATION, 0)
    }

    fun isLoggedIn (): Boolean {
        val doesHaveToken = !preferences.getString(ACCESS_TOKEN, "").isNullOrEmpty()
        val isTokenValid = getExpiration() < System.currentTimeMillis()

        return doesHaveToken && isTokenValid
    }


    @Keep
    companion object {
        private const val ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "SPOTIFY_REFRESH_TOKEN"
        private const val EXPIRATION = "SPOTIFY_EXPIRATION"
    }
}