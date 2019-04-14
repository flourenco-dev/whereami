package com.fabiolourenco.whereami.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fabiolourenco.whereami.R
import com.fabiolourenco.whereami.data.model.api.Suggestion
import kotlinx.android.synthetic.main.item_nearby_place.view.*

class PlacesNearbyAdapter internal constructor(private val placesNearby: List<Suggestion>,
                                               private val listener: OnItemClickListener)
    : RecyclerView.Adapter<PlacesNearbyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nearby_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(placesNearby[position])
    }

    override fun getItemCount() = placesNearby.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Suggestion) {

            itemView.labelText.text = item.getFormattedStreet()
            itemView.distanceText.text = item.getFormattedDistance()

            itemView.setOnClickListener { listener.onItemClick(item) }
        }
    }

    internal interface OnItemClickListener {
        fun onItemClick(placeNearby: Suggestion)
    }
}