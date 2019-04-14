package com.fabiolourenco.whereami.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fabiolourenco.whereami.App
import com.fabiolourenco.whereami.data.model.database.Place

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

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
}