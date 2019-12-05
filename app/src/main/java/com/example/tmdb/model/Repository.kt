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

/*
    Test
 */
open class BaseRepository{
    fun x(){
        test(fun (): Int{
            Log.i("temp", "tem")
            return 5
        })
    }

    fun test(call: () -> Int): Int{
        val u = safeApiCallX(call)
        val z = safeApiCallX{4}
        val t = safeApiCallX(fun (): Int = 4)
        val v = safeApiCallX(fun (): Int { return 4 })

        return 4
    }

    fun <T> safeApiCallX(call: () -> T): T{
//        var u = call
        var z: Int = 0

        val u = GlobalScope.launch{
            val u = temp1{ ->
                GlobalScope.async {
                    delay(4)
                    return@async 5
                }
            }

            z = u.await()

            val t = temp1{
                delay(100)
                return@temp1 5
            }

        }

        val x = GlobalScope.launch{
            temp{ 4 }
        }

        return call.invoke()
    }

    suspend fun <T> temp(call: suspend () -> T): T = call.invoke()

    suspend fun <T> temp1(call: suspend () -> T): T {
        return call.invoke()
    }

    suspend fun <T:Any> safeApiCall(call: suspend () -> T, errorMsg: String): T{
        return call.invoke()
    }
}

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

                Log.i("DUPA", it.baseUrl)
                Log.i("DUPA", it.posterSizes!![0])

                return@async "${it.baseUrl}${it.posterSizes!![0]}/$path".toHttpUrlOrNull()

//                val r = HttpUrl.Builder()
//                    .host(it.baseUrl!!)
//                    .addPathSegment(it.posterSizes!![0])
//                    .addPathSegment(path)
//                    .build()
//
//                return@async r
                //                if(it.baseUrl != null && it.posterSizes != null){
                //                    urlBuilder.host(it.baseUrl!!)
                //                    urlBuilder.addPathSegment(it.posterSizes!![0])
                //                    url = urlBuilder.build()
                //                }
                //                return url
            }
        }
    }.await()


    /* TEST */
    suspend fun getPopularMovies2(): Deferred<List<TmdbMovie>>{
        val u = api.getPopularMovies()

        val zu = GlobalScope.async {
            u.results!!
        }

        return zu
    }

    suspend fun getPopularMovies3(): Deferred<List<TmdbMovie>>{
        val r = coroutineScope.async{
            api.getPopularMovies().results!!
        }

        return r
    }

    suspend fun test(){
        val s = withContext(Dispatchers.IO){
            1
        }

        val u = withContext(Dispatchers.IO){
            launch{
                1
            }
        }

        val z = withContext(Dispatchers.IO){
            async{
                1
            }
        }.await()

        val t = coroutineScope{
            async { 1 }
        }
    }
    /* END OF TEST */
}