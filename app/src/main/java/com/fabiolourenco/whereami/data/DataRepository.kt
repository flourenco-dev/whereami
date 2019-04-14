package com.fabiolourenco.whereami.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.fabiolourenco.whereami.data.api.GeocodeApi
import com.fabiolourenco.whereami.data.api.SuggestionsApi
import com.fabiolourenco.whereami.data.database.WhereAmIDatabase
import com.fabiolourenco.whereami.data.model.api.DisplayPosition
import com.fabiolourenco.whereami.data.model.api.GeocodeResponse
import com.fabiolourenco.whereami.data.model.api.Suggestion
import com.fabiolourenco.whereami.data.model.api.SuggestionsResponse
import com.fabiolourenco.whereami.data.model.database.Place
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executors

/**
 * Application data repository to manage all data of the app
 */
class DataRepository private constructor(private val database: WhereAmIDatabase) {

    private val dataExecutor = Executors.newSingleThreadExecutor()
    private val observableFavorites: MediatorLiveData<List<Place>> = MediatorLiveData()

    val favorites: LiveData<List<Place>>
        get() = observableFavorites

    init {
        observableFavorites.addSource(database.placeDao().loadFavorites()) { favorites ->
            observableFavorites.value = favorites
        }
    }

    private val suggestionsRetrofit = Retrofit.Builder()
        .baseUrl("http://autocomplete.geocoder.api.here.com/6.2/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val geocodeRetrofit = Retrofit.Builder()
        .baseUrl("https://geocoder.api.here.com/6.2/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val suggestionsApi = suggestionsRetrofit.create(SuggestionsApi::class.java)
    private val geocodeApi = geocodeRetrofit.create(GeocodeApi::class.java)

    val suggestions: MutableLiveData<List<Suggestion>> = MutableLiveData()
    val nearbyPlaces: MutableLiveData<List<Suggestion>> = MutableLiveData()
    val geocodeLocation: MutableLiveData<DisplayPosition> = MutableLiveData()

    fun getSuggestions(query: String) {
        suggestionsApi.getSuggestions(query).enqueue(object : Callback<SuggestionsResponse> {
            override fun onFailure(call: Call<SuggestionsResponse>, t: Throwable) {
                suggestions.value = null
                // handle error - snackBar
            }

            override fun onResponse(call: Call<SuggestionsResponse>, response: Response<SuggestionsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    suggestions.value = response.body()!!.suggestions
                } else {
                    suggestions.value = null
                    // handle error - snackBar
                }
            }
        })
    }

    fun getNearbyPlaces(query: String, latitude: Double, longitude: Double) {
        suggestionsApi.getNearbyPlaces(query, "$latitude,$longitude")
            .enqueue(object : Callback<SuggestionsResponse> {
                override fun onFailure(call: Call<SuggestionsResponse>, t: Throwable) {
                    nearbyPlaces.value = null
                    // handle error - snackBar
                }

                override fun onResponse(call: Call<SuggestionsResponse>, response: Response<SuggestionsResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        nearbyPlaces.value = response.body()!!.suggestions
                    } else {
                        nearbyPlaces.value = null
                        // handle error - snackBar
                    }
                }
            })
    }

    fun getGeocode(locationId: String) {
        geocodeApi.getCoordinates(locationId).enqueue(object : Callback<GeocodeResponse> {
            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                geocodeLocation.value = null
            }

            override fun onResponse(call: Call<GeocodeResponse>, response: Response<GeocodeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    geocodeLocation.value = response.body()!!.getGeocodeLocation()
                } else {
                    geocodeLocation.value = null
                    // handle error - snackBar
                }
            }

        })
    }

    fun insertFavorite(place: Place) {
        dataExecutor.execute {
            database.placeDao().insertFavorite(place)
        }
    }

    fun deleteFavorite(place: Place) {
        dataExecutor.execute {
            database.placeDao().deleteFavorite(place)
        }
    }

    companion object {
        private var instance: DataRepository? = null

        /**
         * Create DataRepository as singleton to prevent having multiple instances
         */
        fun getInstance(database: WhereAmIDatabase): DataRepository {
            if (instance == null) {
                synchronized(DataRepository::class) {
                    if (instance == null) {
                        instance = DataRepository(database)
                    }
                }
            }
            return instance!!
        }
    }
}