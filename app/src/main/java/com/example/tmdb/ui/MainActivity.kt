package com.example.tmdb.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tmdb.R
import com.example.tmdb.model.MoviesRepository
import com.example.tmdb.model.MoviesViewModel
import com.example.tmdb.model.MoviesViewModelFactory
import com.example.tmdb.service.TmdbApiFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


data class TmdbMovieTest(
    val id: Int,
    val vote_average: Double,
    val title: String,
    val overview: String,
    val adult: Boolean
)

data class TmdbMovieResponseTest(
    var results: List<TmdbMovieTest>,
    var page: Int,
    var total_results: Int,
    var total_pages: Int
)

interface TmdbService{
    @GET("3/movie/popular")
    fun getPopularMovie(): Call<TmdbMovieResponseTest>
}

class ApiInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val newUrl = chain.request().url
            .newBuilder()
            .addQueryParameter("api_key", "5670a072865c4667b914bf52598d8344")
            .build()

        val request = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}

data class Todo(
    val id: Int = 0,
    val title: String = "",
    val completed: Boolean = false
)

interface Webservice {
    @GET("/todos/{id}")
    fun getTodo(@Path(value = "id") todoId: Int): Call<Todo>
}

class MainActivity : AppCompatActivity() {

    val webservice by lazy{
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(Webservice::class.java)
    }

    val job = Job()
    val localScope= CoroutineScope(Dispatchers.Main + job)

    var callbackManager: CallbackManager? = null

    var shareDialog: ShareDialog? = null

    suspend fun test(): Int{
        delay(1000)
        return 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val vmFactory = MoviesViewModelFactory(MoviesRepository(TmdbApiFactory.create()))
        val vm = ViewModelProviders.of(this, vmFactory).get(MoviesViewModel::class.java)

        vm.movies.observe(this, Observer{
            it?.let{
                Log.i("DUPA", it[0].toString())

                localScope.launch{
                    val movie = vm.getMovie(it[0].id)//!!.posterFullPath
                    movie?.let {
                        Log.i("DUPA", it.toString())
                    }
                }
            }
        })

        button.setOnClickListener {

            vm.loadPopularMovies(2)
//
//            val tmdbApiClient = TmdbApiFactory.create()
//            val repository = MoviesRepository(api = tmdbApiClient)
//
//            localScope.launch {
//
//                val u =tmdbApiClient.getTopRatedMovies()
//                val x = repository.getPopularMovies()
//                Log.i("String", "String")
//            }


//            localScope.launch {
//
//
//                withContext(Dispatchers.IO){
//
//                    val total = tmdbApiClient.getPopularMovies().totalResults
//                    Log.i("DUPA", "$total")
////                    tmdbApiClient.getPopularMovies().enqueue(object: Callback<TmdbMoviesResponse>{
////                        override fun onResponse(call: Call<TmdbMoviesResponse>, response: Response<TmdbMoviesResponse>) {
////                            val movieResponse = response.body()
////                            Log.i("DUPA", "${movieResponse?.page}")
////                        }
////
////                        override fun onFailure(call: Call<TmdbMoviesResponse>, t: Throwable) {
////                            t.printStackTrace()
////                        }
////                    })
//                }
////                tmdbApi.getPopularMovies().
////                val response = tmdbApi.getPopularMovies().execute().body()
////                Log.i("DUPA", "${response?.totalPages}")
//            }
//            val client = webservice
//            client.getTodo(1).enqueue(object: Callback<Todo>{
//                override fun onFailure(call: Call<Todo>, t: Throwable) {
//                    t.printStackTrace()
//                }
//
//                override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
//                    if(response.isSuccessful){
//                        val x = response.body()
//                        Log.i("DUPA", x.toString())
//                    }
//                }
//            })


            return@setOnClickListener

            val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.themoviedb.org")
//                .addPathSegment("3")
//            .addQueryParameter("api_key", "5670a072865c4667b914bf52598d8344")
                .build()

            Log.i("TMDB", url.toString())

            val apiInterceptor = ApiInterceptor()

            val client = OkHttpClient
                .Builder()
                .addInterceptor(apiInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

            val service = retrofit.create(TmdbService::class.java)

            val x = service.getPopularMovie().enqueue(object: Callback<TmdbMovieResponseTest>{
                override fun onResponse(call: Call<TmdbMovieResponseTest>, response: Response<TmdbMovieResponseTest>) {
                    val popularMovies = response.body()

                }

                override fun onFailure(call: Call<TmdbMovieResponseTest>, t: Throwable) {
                    Log.i("TMDB", t.message)
                }
            })
        }


        callbackManager = CallbackManager.Factory.create()

        login_button.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(loginResult: LoginResult){

                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired
            }

            override fun onCancel() {
                Log.i("DUPA", "")
            }

            override fun onError(error: FacebookException?) {
                Log.i("DUPA", "")
            }
        })

        shareDialog = ShareDialog(this)
        shareDialog!!.registerCallback(callbackManager, object: FacebookCallback<Sharer.Result>{
            override fun onSuccess(loginResult: Sharer.Result?){
                Log.i("DUPA", "")
            }

            override fun onCancel() {
                Log.i("DUPA", "")
            }

            override fun onError(error: FacebookException?) {
                Log.i("DUPA", "")
            }
        })

        button2.setOnClickListener {
            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build()

                shareDialog!!.show(linkContent);
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }
}
