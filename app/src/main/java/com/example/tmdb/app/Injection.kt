package com.example.tmdb.app

import androidx.lifecycle.Lifecycle
import com.example.tmdb.repository.MoviesRepository
import com.example.tmdb.repository.Repository
import com.example.tmdb.service.TmdbApi
import com.example.tmdb.service.TmdbApiFactory
import com.example.tmdb.ui.movies.MoviesViewModelFactory

object Injection{
    private fun provideRepository(api: TmdbApi): Repository{
        return MoviesRepository(api)
    }

    private fun provideApi(): TmdbApi {
        return TmdbApiFactory.create()
    }

    fun provideViewModelFactory(lifecycle: Lifecycle): MoviesViewModelFactory{
        val repository = provideRepository(provideApi())
        repository.registerLifecycle(lifecycle)
        return MoviesViewModelFactory(repository, lifecycle)
    }
}