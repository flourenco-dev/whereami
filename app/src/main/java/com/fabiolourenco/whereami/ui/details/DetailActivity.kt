package com.fabiolourenco.whereami.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fabiolourenco.whereami.R
import com.fabiolourenco.whereami.data.model.database.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.android.synthetic.main.layout_detail_container.*
import timber.log.Timber

class DetailActivity : FragmentActivity() {

    private lateinit var detailViewModel: DetailViewModel

    private var place: Place? = null
    // Save the google map reference to operate it
    private var map: GoogleMap? = null

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        place = intent.extras?.getParcelable(extraPlace)
        if (place == null) {
            // show error
            onBackPressed()
        }

        favoriteButton?.setOnClickListener {
            place?.let { place ->
                if (isFavorite) {
                    detailViewModel.deleteFavorite(place)

                } else {
                    detailViewModel.insertFavorite(place)
                }
            }
        }

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        detailViewModel.favorites.observe(this, Observer { favorites ->
            Timber.d("favorites updated - list: $favorites")
            val result = favorites?.find {
                it.locationId == place?.locationId
            }

            if (result == null) {
                // show remove from favorites
                favoriteButton?.text = getString(R.string.add_to_favorites)
                favoriteButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_border_black_24dp)
                isFavorite = false

            } else {
                // show add to favorites
                favoriteButton?.text = getString(R.string.remove_from_favorites)
                favoriteButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_black_24dp)
                isFavorite = true
            }
        })

        loadUi()
    }

    private fun loadUi() {
        streetText.text = place?.getFormattedLabel()

        postalCodeText.text = place?.getFormattedPostalCode()
        if (TextUtils.isEmpty(postalCodeText.text)) {
            postalCodeText.isVisible = false
        }

        coordinatesText.text = place?.getFormattedCoordinates()
        if (TextUtils.isEmpty(coordinatesText.text)) {
            coordinatesText.isVisible = false

        } else {
            loadMap()
        }

        distanceText.text = place?.getFormattedDistance()
        if (TextUtils.isEmpty(distanceText.text)) {
            distanceText.isVisible = false
        }

    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync {
            map = it

            place?.getLatLng()?.let { latLng ->
                it.addMarker(
                    MarkerOptions()
                        .position(latLng))
                it.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                it.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))
            }
        }
    }

    companion object {
        private const val extraPlace = "extraPlace"
        private const val DEFAULT_ZOOM = 30f

        fun getDetailIntent(context: Context, place: Place)
                = Intent(context, DetailActivity::class.java).apply {
            putExtra(extraPlace, place)
        }
    }
}
