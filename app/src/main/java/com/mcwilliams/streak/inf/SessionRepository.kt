package com.mcwilliams.streak.inf

import android.content.Context
import android.content.SharedPreferences
import com.mcwilliams.streak.R
import com.mcwilliams.streak.inf.model.GrantType
import com.mcwilliams.streak.inf.model.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SessionRepository @Inject constructor(
    val context: Context,
    private val session: Session
) : ISessionRepository {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    override suspend fun getFirstTokens(code: String): TokenResponse {
        return withContext(context = Dispatchers.IO) {
            val firstToken = session.getFirstToken(
                CLIENT_ID,
                CLIENT_SECRET,
                code,
                GrantType.AUTHORIZATION_CODE.toString()
            )
            if (firstToken.athlete != null) {
//                athleteRepo.saveAthlete(firstToken.athlete)
            }

            setAccessToken(firstToken.access_token)
            setRefreshToken(firstToken.refresh_token)
            firstToken
        }
    }

    override suspend fun refreshToken() : String {
        return withContext(context = Dispatchers.IO) {
            val newTokens = session.refreshToken(
                CLIENT_ID,
                CLIENT_SECRET,
                getRefreshToken(),
                GrantType.REFRESH_TOKEN.toString()
            )

            setAccessToken(newTokens.access_token)
            setRefreshToken(newTokens.refresh_token)
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

    fun logOff (){
        preferences.edit().remove(ACCESS_TOKEN).apply()
        preferences.edit().remove(REFRESH_TOKEN).apply()
    }

    companion object {
        const val CLIENT_ID = 66172
        const val CLIENT_SECRET = "9d8bc5846db6c2df5750f0a130fd88c445b0b363"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val EXPIRATION = "EXPIRATION"
    }

}