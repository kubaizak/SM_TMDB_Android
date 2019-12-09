package com.example.tmdb.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tmdb.R
import com.example.tmdb.app.Injection
import com.example.tmdb.repository.MoviesRepository
import com.example.tmdb.ui.movies.MoviesViewModel
import com.example.tmdb.ui.movies.MoviesViewModelFactory
import com.example.tmdb.service.TmdbApiFactory
import com.example.tmdb.ui.movies.MoviesType
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

var x: Int? = null

suspend fun doSomethingUseful(i: Int): Int {
    if(x == null) {
        Log.i("TEST", "Time consuming operation: START: $i")
        delay(1000L + i) // pretend we are doing something useful here
        x = 13
        Log.i("TEST", "Time consuming operation: DONE: $i")
    }

    Log.i("TEST", "just give me a value($i): $x")
    return x!!
}

class MainActivity : AppCompatActivity() {

    val job = Job()
    val localScope= CoroutineScope(Dispatchers.Main + job)

    var callbackManager: CallbackManager? = null

    var shareDialog: ShareDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vmFactory = Injection.provideViewModelFactory(lifecycle)
        val vm = ViewModelProviders.of(this, vmFactory).get(MoviesViewModel::class.java)

        button.setOnClickListener {
            GlobalScope.launch(Dispatchers.Unconfined){
                // synchronous manner
//                listOf(1, 2, 3, 4, 5).forEachIndexed { index, i ->
//                    Log.i("TEST", "forEach START: $index")
//                    var result: Int = 0
//                    val time = measureTimeMillis {
//                        result = withContext(Dispatchers.Unconfined) {
//                            doSomethingUseful(index)
//                        }
//                    }
//                    Log.i("TEST", "forEach END: $index results in $result, took: $time")
//                }

                // parallel
                val time = measureTimeMillis {
                    Log.i("TEST", "START")
                    val result1 = async {
                        doSomethingUseful(100)
                    }
                    val result2 = async {
                        doSomethingUseful(50)
                    }
                    val result3 = async {
                        doSomethingUseful(200)
                    }

                    Log.i("TEST", "BEFORE AWAITS")
                    Log.i(
                        "TEST",
                        "END: results ($x) in ${result1.await()}, ${result2.await()}, ${result3.await()}, VALUE: ${x}"
                    )
                }
                Log.i("TEST", "TIME: $time")
            }
//            vm.loadMovies(MoviesType.POPULAR)
        }

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
