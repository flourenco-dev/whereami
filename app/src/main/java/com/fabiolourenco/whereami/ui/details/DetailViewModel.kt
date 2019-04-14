package com.fabiolourenco.whereami.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fabiolourenco.whereami.App
import com.fabiolourenco.whereami.data.model.database.Place

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    // Use instance of DataRepository to handle data
    private val repository = (application as App).repository

    // MediatorLiveData can observe other LiveData objects and react on their emissions
    private var observableFavorites: MediatorLiveData<List<Place>> = MediatorLiveData()

    val favorites: LiveData<List<Place>>
        get() = observableFavorites

    init {
        observableFavorites.value = null
        observableFavorites.addSource(repository.favorites) {
            observableFavorites.value = it
        }
    }

    fun insertFavorite(place: Place) {
        repository.insertFavorite(place)
    }

    fun deleteFavorite(place: Place) {
        repository.deleteFavorite(place)
    }
}
