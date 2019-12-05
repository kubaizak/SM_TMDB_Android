package com.example.tmdb.service

import android.net.Uri
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApiFactory {
    private val authInterceptor = object: Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            val newUrl = request.url.newBuilder()
                .addQueryParameter("api_key", "5670a072865c4667b914bf52598d8344")
                .build()

            Log.i("URL", newUrl.toString())

            request = request.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(request)
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val baseUrl = HttpUrl.Builder()
        .scheme("https")
        .host("api.themoviedb.org")
        .addPathSegment("3")
        .build()

    fun create(): TmdbApi{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()

        return retrofit.create(TmdbApi::class.java)
    }
}