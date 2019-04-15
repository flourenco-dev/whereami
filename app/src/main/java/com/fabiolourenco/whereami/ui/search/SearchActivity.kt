package com.fabiolourenco.whereami.ui.search

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.fabiolourenco.whereami.R
import com.fabiolourenco.whereami.data.model.api.Suggestion
import com.fabiolourenco.whereami.data.model.database.Place
import com.fabiolourenco.whereami.ui.details.DetailActivity
import com.fabiolourenco.whereami.ui.favorites.FavoritesFragment
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search.*
import timber.log.Timber

class SearchActivity : AppCompatActivity(), PlacesNearbyAdapter.OnItemClickListener, OnSuccessListener<Location> {

    // Provider to get device location information
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // ViewModel to track Search data requests
    private lateinit var searchViewModel: SearchViewModel

    private var location: Location? = null
    private var selectedPlace: Suggestion? = null

    private val placesNearby: MutableList<Suggestion> = ArrayList()
    private lateinit var adapter: PlacesNearbyAdapter

    // This flag will be used to avoid going to DetailActivity when the orientation changes
    private var allowDetailScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adapter = PlacesNearbyAdapter(placesNearby, this)
        adapter.setHasStableIds(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        loadListeners()
        loadLocation()
        loadViewModels()

        // Hack to partially fix heavy loading time of Google Maps
        Thread {
            MapView(this).apply {
                onCreate(null)
                onPause()
                onDestroy()
            }
        }
    }

    override fun onItemClick(placeNearby: Suggestion) {
        placeNearby.locationId?.let {
            allowDetailScreen = true
            selectedPlace = placeNearby
            // Get geocode for this address
            searchViewModel.getGeocodeLocation(it)
            loadingView.isVisible = true

        } ?: run {
            Snackbar.make(mainContainer, "Unable to make request, bad data format!", Snackbar.LENGTH_SHORT)
        }
    }

    override fun onSuccess(lastLocation: Location?) {
        location = lastLocation

        lastLocation?.let {
            searchView.isVisible = true

        } ?: run {
            Snackbar.make(mainContainer, "No last known location found!", Snackbar.LENGTH_SHORT)
            // show message to the user and update UI
            searchView.isInvisible = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermissions()
                } else {
                    // This call requires permission checking, which by now should be accepted
                    if (requestLocationPermissions()) {
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener(this)

                        } catch (e: SecurityException) {
                            // Don't worry about logging the exception, permissions are guaranteed by requestLocationPermissions()
                            // This try catch block is just used to remove IDE warnings
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            && checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_CODE)
            false

        } else {
            true
        }

    private fun loadListeners() {
        searchView.setOnQueryChangeListener { _, newQuery ->
            // Only request Suggestions if there are more than 3 words queried
            if (newQuery.length >= 3) {
                searchViewModel.getSuggestions(newQuery)
                searchView.showProgress()

            } else {
                searchView.clearSuggestions()
                searchView.hideProgress()
            }
        }

        searchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {
                loadingView.isVisible = true
                searchView.hideProgress()

                // Get the places related with the query with the distance related with th device's location
                currentQuery?.let {
                    searchViewModel.getNearbyPlaces(currentQuery,
                        location?.latitude ?: 0.0,
                        location?.longitude ?: 0.0)
                }

            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                // Update search text with the clicked suggestion
                searchSuggestion?.let {
                    searchView.setSearchText(searchSuggestion.body)
                }
            }
        })

        searchView.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sort_az -> searchViewModel.sortNearbyPlacesByAz()
                R.id.action_sort_distance -> searchViewModel.sortNearbyPlacesByDistance()
            }
        }

        favoritesFab.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.findFragmentByTag("favorites")?.let {
                // If fragment was already added, remove it
                fragmentTransaction.remove(it)
            }
            fragmentTransaction.addToBackStack(null)
            FavoritesFragment().show(fragmentTransaction, "favorites")
        }

        loadingView.setOnClickListener {
            // Do nothing... Just used to intercept other clicks
        }
    }

    private fun loadLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Right now the only location used will be last know one, but in the future it can be
        // listened to location updates, if required, and this callback will be moved to the view model
        if (requestLocationPermissions()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener(this)

            } catch (e: SecurityException) {
                // Don't worry about logging the exception, permissions are guaranteed by requestLocationPermissions()
                // This try catch block is just used to remove IDE warnings
            }
        }
    }

    private fun loadViewModels() {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.suggestions.observe(this, Observer { suggestions ->
            Timber.d("suggestions updated - list: $suggestions")
            if (searchView.isSearchBarFocused) {
                searchView.swapSuggestions(suggestions ?: ArrayList())
                searchView.hideProgress()
            }
        })

        searchViewModel.nearbyPlaces.observe(this, Observer { nearbyPlaces ->
            Timber.d("nearby places updated - list: $nearbyPlaces")
            placesNearby.clear()
            placesNearby.addAll(nearbyPlaces ?: ArrayList())
            adapter.notifyDataSetChanged()
            // The list was updated so scroll back to top
            recyclerView.smoothScrollToPosition(0)
            loadingView.isVisible = false

            if (CollectionUtils.isEmpty(placesNearby)) {
                recyclerView.isInvisible = true
                emptyView.isVisible = true

            } else {
                recyclerView.isVisible = true
                emptyView.isVisible = false
            }
        })

        searchViewModel.geocodeLocation.observe(this, Observer { position ->
            Timber.d("geocode location updated - geocode: $position")
            if (allowDetailScreen) {
                position?.let {
                    val place = Place(
                        selectedPlace?.locationId ?: "",
                        selectedPlace?.label ?: "",
                        selectedPlace?.address,
                        selectedPlace?.distance ?: 0,
                        it
                    )

                    startActivity(DetailActivity.getDetailIntent(this, place))

                } ?: run {
                    Snackbar.make(mainContainer, "Unable to retrieve position!", Snackbar.LENGTH_SHORT)
                }

                loadingView.isVisible = false
                allowDetailScreen = false
            }
        })

        searchViewModel.favorites.observe(this, Observer { favorites ->
            favoritesFab.isVisible = !CollectionUtils.isEmpty(favorites)
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_CODE = 0
    }
}
