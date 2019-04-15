package com.fabiolourenco.whereami.ui.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabiolourenco.whereami.R
import com.fabiolourenco.whereami.data.model.database.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : DialogFragment(), FavoritesAdapter.OnItemClickListener {

    private lateinit var favoritesViewModel: FavoritesViewModel
    // Save the google map reference to operate it
    private var map: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val favorites: MutableList<Place> = ArrayList()
    private lateinit var adapter: FavoritesAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.favoritesMapContainer) as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.favoritesMapContainer, mapFragment!!)
                .commitAllowingStateLoss()
        }

        mapFragment?.getMapAsync {
            map = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoritesAdapter(favorites, this)
        adapter.setHasStableIds(true)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        favoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        favoritesViewModel.favorites.observe(this, Observer {  favoriteList ->
            favorites.clear()
            favorites.addAll(favoriteList ?: ArrayList())
            adapter.notifyDataSetChanged()
        })
    }

    override fun onItemClick(favorite: Place) {
        map?.apply {
            val latLng = favorite.getLatLng()
            addMarker(
                MarkerOptions()
                    .position(latLng))
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 12f
    }
}
