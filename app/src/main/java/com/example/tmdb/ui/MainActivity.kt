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

class MainActivity : AppCompatActivity() {

    val job = Job()
    val localScope= CoroutineScope(Dispatchers.Main + job)

    var callbackManager: CallbackManager? = null

    var shareDialog: ShareDialog? = null


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

                shareDialog!!.show(linkContent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
