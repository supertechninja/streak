package com.mcwilliams.streak.inf.spotify

import androidx.annotation.Keep
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

@Keep
class SpotifyTokenInterceptor @Inject constructor(private val spotifySessionRepository: SpotifySessionRepository) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
//        if (stravaSessionRepository.getExpiration() > System.currentTimeMillis()) {
//            runBlocking {
//                stravaSessionRepository.refreshToken()
//            }
//        }

        val token = spotifySessionRepository.getAccessToken()
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .method(original.method(), original.body())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}