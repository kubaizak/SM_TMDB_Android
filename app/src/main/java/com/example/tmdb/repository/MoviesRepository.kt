package com.example.tmdb.repository

import android.util.Log
import androidx.lifecycle.*
import com.example.tmdb.model.TmdbImagesConfiguration
import com.example.tmdb.model.TmdbMovie
import com.example.tmdb.model.TmdbMovieDetails
import com.example.tmdb.model.TmdbMoviesResponse
import com.example.tmdb.service.TmdbApi
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.coroutines.CoroutineContext

var i = 0

class MoviesRepository(private val api: TmdbApi): Repository, CoroutineScope{
    private var imgConfig: TmdbImagesConfiguration? = null

    private val TAG = MoviesRepository::class.java.simpleName

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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

//    override suspend fun getMovieImageUrl(path: String): HttpUrl? = async{
//            getImageConfiguration()?.let{
//                it.baseUrl ?: return@let null
//                it.posterSizes ?: return@let null
//
//                return@async "${it.baseUrl}${it.posterSizes!![0]}/$path".toHttpUrlOrNull()
//            }
//        }.await()

    override suspend fun getMovieImageUrl(path: String): HttpUrl? {
        return async {
            val conf = getImageConfiguration()
            Log.i("DUPA", "${i}: ${conf.toString()}")
            conf?.let{ conf
                conf.baseUrl ?: return@let null
                conf.posterSizes ?: return@let null

                return@async "${conf.baseUrl}${conf.posterSizes!![0]}/$path".toHttpUrlOrNull()
            }
        }.await()
    }

    override fun registerLifecycle(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
    }

    private fun getMovies(getMoviesFun: suspend () -> TmdbMoviesResponse): LiveData<List<TmdbMovie>>{
        val liveData = MutableLiveData<List<TmdbMovie>>()

        launch{
            liveData.value = getMoviesFun.invoke().results!!
        }

        return liveData
    }

    private suspend fun getImageConfiguration(): TmdbImagesConfiguration?{
        if(imgConfig == null) {
            Log.i("DUPA", "getImageConfigurationAsync: START")
            imgConfig = async {
                api.getConfiguration().images

            }.await()
            Log.i("DUPA", "getImageConfigurationAsync: DONE")
        }

        Log.i("DUPA", "getImageConfiguration")
        return imgConfig
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