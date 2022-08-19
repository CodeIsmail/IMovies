package dev.codeismail.imovies.data

import android.content.Context
import android.util.Log
import dev.codeismail.imovies.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val httpUrl = request.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()

        request = request.newBuilder().apply {
            addHeader("Content-Type", "application/json")
            addHeader(
                "Version-Info",
                context.packageName + "/Android/" +
                        BuildConfig.VERSION_CODE
            )
            addHeader("Source", "android")
            url(httpUrl)

        }.build()
        val d = request.url.toString()
        Log.d("Hello", d)
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (ex: Exception) {
            throw IOException(ex.localizedMessage)
        }

        return response
    }
}