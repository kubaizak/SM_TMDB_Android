package com.example.tmdb.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoviesViewModelFactory (private val repository: MoviesRepositoryInterface): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MoviesViewModel::class.java)){
            return MoviesViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}