package com.example.tmdb.model

import android.provider.Settings
import android.util.Log
import androidx.lifecycle.*
import com.example.tmdb.service.TmdbApi
import com.example.tmdb.service.TmdbApiFactory
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import retrofit2.http.Url
import java.net.URL
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URI
import kotlin.coroutines.CoroutineContext


interface MoviesRepositoryInterface: LifecycleObserver{
    fun getPopularMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getTopRatedMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getUpcomingMovies(page: Int = 1): LiveData<List<TmdbMovie>>

    fun getMovie(id: Int): LiveData<TmdbMovieDetails?>

    fun getMoviePosterUrl(movie: TmdbMovieDetails): LiveData<HttpUrl?>

    fun registerLifecycle(lifecycle: Lifecycle)
}

class MoviesRepository(private val api: TmdbApi): MoviesRepositoryInterface, CoroutineScope{
    private var imgConfig: TmdbImagesConfiguration? = null

    private val TAG = MoviesRepository::class.java.simpleName

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private fun getMovies(getMoviesFun: suspend () -> TmdbMoviesResponse): LiveData<List<TmdbMovie>>{
        val liveData = MutableLiveData<List<TmdbMovie>>()

        launch{
            liveData.value = getMoviesFun.invoke().results!!
        }

        return liveData
    }

    override fun getPopularMovies(page: Int): LiveData<List<TmdbMovie>> {
        return getMovies { api.getPopularMovies(page) }
    }

    override fun getTopRatedMovies(page: Int): LiveData<List<TmdbMovie>>{
        return getMovies { api.getTopRatedMovies(page) }
    }

    override fun getUpcomingMovies(page: Int): LiveData<List<TmdbMovie>> {
        return getMovies { api.getUpcomingMovies(page) }
    }

    override fun getMovie(id: Int): LiveData<TmdbMovieDetails?> {
        val liveData = MutableLiveData<TmdbMovieDetails?>()

        launch{
            liveData.value = api.getMovie(id)
        }

        return liveData
    }

    private suspend fun getImageConfiguration(): TmdbImagesConfiguration?{
        if(imgConfig == null) {
            imgConfig = async {
                api.getConfiguration().images

            }.await()
        }

        return imgConfig
    }

    override fun getMoviePosterUrl(movie: TmdbMovieDetails): LiveData<HttpUrl?> {
        val liveData = MutableLiveData<HttpUrl?>()

        launch{
            movie.posterPath?.let{ path ->
                liveData.value = getImageConfiguration()?.let{
                    it.baseUrl ?: return@let null
                    it.posterSizes ?: return@let null

                    return@let "${it.baseUrl}${it.posterSizes!![0]}/$path".toHttpUrlOrNull()
                }
            }
        }

        return liveData
    }

    override fun registerLifecycle(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun cancelJob(){
        Log.d(TAG, "cancelJob()")

        if(job.isActive) {
            Log.d(TAG, "Job active, cancelling")
            job.cancel()
        }
    }
}