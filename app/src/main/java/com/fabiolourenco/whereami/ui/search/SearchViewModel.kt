package com.fabiolourenco.whereami.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fabiolourenco.whereami.App
import com.fabiolourenco.whereami.data.model.api.DisplayPosition
import com.fabiolourenco.whereami.data.model.api.Suggestion
import com.fabiolourenco.whereami.data.model.database.Place

class SearchViewModel(application: Application): AndroidViewModel(application) {

    // Use instance of DataRepository to handle data
    private val repository = (application as App).repository

    // MediatorLiveData can observe other LiveData objects and react on their emissions
    private var observableSuggestions: MediatorLiveData<List<Suggestion>> = MediatorLiveData()
    private var observableNearbyPlaces: MediatorLiveData<List<Suggestion>> = MediatorLiveData()
    private var observableGeocodeLocation: MediatorLiveData<DisplayPosition> = MediatorLiveData()
    private var observableFavorites: MediatorLiveData<List<Place>> = MediatorLiveData()

    val suggestions: LiveData<List<Suggestion>>
        get() = observableSuggestions
    val nearbyPlaces: LiveData<List<Suggestion>>
        get() = observableNearbyPlaces
    val geocodeLocation: LiveData<DisplayPosition>
        get() = observableGeocodeLocation
    val favorites: LiveData<List<Place>>
        get() = observableFavorites

    init {
        observableSuggestions.value = null
        observableSuggestions.addSource(repository.suggestions) {
            observableSuggestions.value = it
        }

        observableNearbyPlaces.value = null
        observableNearbyPlaces.addSource(repository.nearbyPlaces) {
            observableNearbyPlaces.value = sortDistance(it ?: mutableListOf())
        }

        observableGeocodeLocation.value = null
        observableGeocodeLocation.addSource(repository.geocodeLocation) {
            observableGeocodeLocation.value = it
        }
        observableFavorites.value = null
        observableFavorites.addSource(repository.favorites) {
            observableFavorites.value = it
        }
    }

    fun getSuggestions(query: String) {
        repository.getSuggestions(query)
    }

    fun getNearbyPlaces(query: String, latitude: Double, longitude: Double) {
        repository.getNearbyPlaces(query, latitude, longitude)
    }

    fun getGeocodeLocation(locationId: String) {
        repository.getGeocode(locationId)
    }

    fun sortNearbyPlacesByAz() {
        observableNearbyPlaces.value?.let {
            observableNearbyPlaces.value = sortAz(it)
        }
    }

    fun sortNearbyPlacesByDistance() {
        observableNearbyPlaces.value?.let {
            observableNearbyPlaces.value = sortDistance(it)
        }
    }

    private fun sortAz(nearbyPlaces: List<Suggestion>): MutableList<Suggestion> {
        val mutableNearbyPlaces: MutableList<Suggestion> = nearbyPlaces.toMutableList()
        mutableNearbyPlaces.sortWith(Comparator { o1: Suggestion, o2: Suggestion ->
            if (o1.label != null && o2.label != null) {
                o1.label.compareTo(o2.label, true)
            } else {
                0
            }
        })

        return mutableNearbyPlaces
    }

    private fun sortDistance(nearbyPlaces: List<Suggestion>): MutableList<Suggestion> {
        val mutableNearbyPlaces: MutableList<Suggestion> = nearbyPlaces.toMutableList()
        mutableNearbyPlaces.sortWith(Comparator { o1: Suggestion, o2: Suggestion ->
            if (o1.distance != null && o2.distance != null) {
                o1.distance - o2.distance
            } else {
                0
            }
        })

        return mutableNearbyPlaces
    }
}