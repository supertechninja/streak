package com.mcwilliams.streak.inf

import com.mcwilliams.streak.inf.model.TokenResponse

interface ISessionRepository {
    suspend fun getFirstTokens(code: String): TokenResponse
    suspend fun refreshToken(): String

    fun setAccessToken(accessToken: String)
    fun getAccessToken(): String

    fun setRefreshToken(refreshToken: String)
    fun getRefreshToken(): String

    fun setExpiration(expiration: Int)
    fun getExpiration(): Int
}