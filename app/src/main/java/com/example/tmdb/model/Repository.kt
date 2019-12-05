package com.example.tmdb.model

import android.provider.Settings
import android.util.Log
import com.example.tmdb.service.TmdbApi
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import retrofit2.http.Url
import java.net.URL
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URI


interface MoviesRepositoryInterface{
    suspend fun getPopularMovies(page: Int = 1): List<TmdbMovie>

    suspend fun getTopRatedMovies(page: Int = 1): List<TmdbMovie>

    suspend fun getUpcomingMovies(page: Int = 1): List<TmdbMovie>

    suspend fun getMovie(id: Int): TmdbMovieDetails?

    suspend fun getMoviePosterUrl(movie: TmdbMovieDetails): HttpUrl?
}

class MoviesRepository(private val api: TmdbApi): MoviesRepositoryInterface{
    private val parentJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override suspend fun getPopularMovies(page: Int): List<TmdbMovie>{
        return api.getPopularMovies(page).results!!
    }

    override suspend fun getTopRatedMovies(page: Int): List<TmdbMovie>{
        return api.getTopRatedMovies().results!!
    }

    override suspend fun getUpcomingMovies(page: Int) = api.getUpcomingMovies().results!!

    override suspend fun getMovie(id: Int) = api.getMovie(id)

    override suspend fun getMoviePosterUrl(movie: TmdbMovieDetails): HttpUrl? = coroutineScope.async {
        movie.posterPath?.let { path ->
            api.getConfiguration().images?.let {
                it.baseUrl ?: return@let null
                it.posterSizes ?: return@let null

                return@async "${it.baseUrl}${it.posterSizes!![0]}/$path".toHttpUrlOrNull()
            }
        }
    }.await()
}