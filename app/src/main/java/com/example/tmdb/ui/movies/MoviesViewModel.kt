package com.example.tmdb.ui.movies

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.tmdb.model.TmdbMovie
import com.example.tmdb.model.TmdbMovieDetails
import com.example.tmdb.repository.Repository
import kotlinx.coroutines.*

enum class MoviesType {
    POPULAR,
    UPCOMING,
    TOP_RATED
}

class MoviesViewModel(private val repository: Repository, private val lifecycle: Lifecycle): ViewModel() {

    private var _movies = MutableLiveData<List<TmdbMovie>>()
    val movies: LiveData<List<TmdbMovie>>
        get() = _movies

    private var moviesType = MoviesType.UPCOMING
    var page = 1
        private set

    private suspend fun addPosterFullPath(movie: TmdbMovie){
        movie.posterPath?.let{
            movie.posterFullPath = repository.getMovieImageUrl(it)
        }
    }

    private fun LoadMoviesWithImagePath(block: () -> LiveData<List<TmdbMovie>>): LiveData<List<TmdbMovie>>{
        //it is a wrapper of livedata result returned from repository
        val liveData = MutableLiveData<List<TmdbMovie>>()

        block.invoke().observe({lifecycle}){
            GlobalScope.launch {
                //TODO: fix only one call to configuration in repository
var i = 0
                it.forEach{
                    Log.i("DUPA", "forEach: ${++i}")
                    addPosterFullPath(it)
                }

                liveData.postValue(it)
                Log.i("DUPA", "DONE")
            }
        }

        return liveData
    }

    private fun loadPopularMovies(page: Int = 1){
        LoadMoviesWithImagePath{
            repository.getPopularMovies(page)
        }
//        repository.getPopularMovies(page).observe({lifecycle}){
//            _movies.value = it
//        }
    }

    private fun loadUpcomingMovies(page: Int = 1){
        LoadMoviesWithImagePath{
            repository.getUpcomingMovies(page)
        }
//        repository.getUpcomingMovies(page).observe({lifecycle}){
//            _movies.value = it
//        }
    }

    private fun loadTopRatedMovies(page: Int = 1){
        LoadMoviesWithImagePath{
            repository.getTopRatedMovies(page)
        }
//        repository.getTopRatedMovies(page).observe({ lifecycle}){
//            _movies.value = it
//        }
    }

    private fun loadPage(page: Int){
        when(moviesType){
            MoviesType.POPULAR -> loadPopularMovies(page)
            MoviesType.UPCOMING -> loadUpcomingMovies(page)
            MoviesType.TOP_RATED -> loadTopRatedMovies(page)
        }
    }

    fun loadMovies(type: MoviesType, page: Int = 1){
        if(moviesType != type){
            moviesType = type
        }

        this.page = page
        loadPage(page)
    }

    fun loadNextPage(){
        page++
        loadPage(page)
    }

    fun loadPrevPage(){
        if(page > 0) {
            page--
            loadPage(page)
        }
    }

    fun getMovie(id: Int): LiveData<TmdbMovieDetails?>{
        return repository.getMovie(id)
    }


}