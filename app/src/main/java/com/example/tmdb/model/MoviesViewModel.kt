package com.example.tmdb.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MoviesViewModel(private val repository: MoviesRepositoryInterface): ViewModel() {

    private val job = Job()
    private val localScope = CoroutineScope(Dispatchers.Main + job)

    private val _movies = MutableLiveData<List<TmdbMovie>>()

    val movies: LiveData<List<TmdbMovie>>
        get() = _movies

    fun loadPopularMovies(page: Int = 1){
        localScope.launch{
            _movies.value = repository.getPopularMovies(page)
        }
    }

    fun loadUpcomingMovies(page: Int = 1){
        localScope.launch{
            _movies.value = repository.getUpcomingMovies(page)
        }
    }

    fun loadTopRatedMovies(page: Int = 1){
        localScope.launch{
            _movies.value = repository.getTopRatedMovies(page)
        }
    }

    suspend fun getMovie(id: Int): TmdbMovieDetails?{
        return localScope.async{
            val movie = repository.getMovie(id)

            if(movie != null){
                movie.posterFullPath = repository.getMoviePosterUrl(movie)
            }

            movie
        }.await()


//        var movie: TmdbMovieDetails? = null
//
//        localScope.launch {
//            movie = repository.getMovie(id)
////            movie?.let{
////                it.posterFullPath = repository.getMoviePosterUrl(it)
////            }
//        }
//
//        return movie
    }
}