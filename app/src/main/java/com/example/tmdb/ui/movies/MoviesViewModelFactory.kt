package com.example.tmdb.ui.movies

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tmdb.repository.Repository

class MoviesViewModelFactory (private val repository: Repository, private val lifecycle: Lifecycle): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MoviesViewModel::class.java)){
            return MoviesViewModel(repository, lifecycle) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}