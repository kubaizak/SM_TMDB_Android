package com.example.tmdb.service

import com.example.tmdb.model.TmdbImagesConfigurationResponse
import com.example.tmdb.model.TmdbMovieDetails
import com.example.tmdb.model.TmdbMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET ("/3/configuration")
    suspend fun getConfiguration(): TmdbImagesConfigurationResponse

    @GET ("/3/movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): TmdbMoviesResponse

    @GET ("/3/movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): TmdbMoviesResponse

    @GET ("/3/movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): TmdbMoviesResponse

    @GET("/3/movie/{id}")
    suspend fun getMovie(@Path("id") id: Int): TmdbMovieDetails?
}

interface TmdbImagesApi{

    @GET("{baseUrl}{size}/{path}")
    suspend fun getImage(
        @Path("baseUrl") baseUrl: String,
        @Path("size") size: String,
        @Path("path") path: String
    )
}