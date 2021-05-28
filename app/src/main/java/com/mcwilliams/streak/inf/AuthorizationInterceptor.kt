package com.mcwilliams.streak.inf

import androidx.annotation.Keep
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

@Keep
class AuthorizationInterceptor @Inject constructor(val sessionRepository: SessionRepository) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (sessionRepository.getExpiration() > System.currentTimeMillis()) {
            runBlocking {
                sessionRepository.refreshToken()
            }
        }

        val token = sessionRepository.getAccessToken()
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .method(original.method(), original.body())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}