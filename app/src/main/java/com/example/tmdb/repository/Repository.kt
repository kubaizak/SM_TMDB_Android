package com.example.tmdb.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.example.tmdb.model.TmdbMovie
import com.example.tmdb.model.TmdbMovieDetails
import okhttp3.HttpUrl

interface Repository: LifecycleObserver {
    fun getPopularMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getTopRatedMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getUpcomingMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getMovie(id: Int): LiveData<TmdbMovieDetails?>

    fun getMoviePosterUrl(movie: TmdbMovieDetails): LiveData<HttpUrl?>

    suspend fun getMovieImageUrl(posterPath: String): HttpUrl?

    fun registerLifecycle(lifecycle: Lifecycle)
}