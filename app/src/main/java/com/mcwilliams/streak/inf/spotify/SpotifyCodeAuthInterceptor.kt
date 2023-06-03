package com.mcwilliams.streak.inf.spotify

import androidx.annotation.Keep
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

@Keep
class SpotifyCodeAuthInterceptor @Inject constructor() :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val base64Encoded = Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Basic $base64Encoded")
            .method(original.method(), original.body())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        const val CLIENT_ID = "0fb6298f96e24dc8a4b80a1109522ef9"
        const val CLIENT_SECRET = "e0ae211b17a4485aaffcd49156ddec01"
    }
}