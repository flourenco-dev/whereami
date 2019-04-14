package com.fabiolourenco.whereami.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fabiolourenco.whereami.R
import com.fabiolourenco.whereami.data.model.database.Place
import kotlinx.android.synthetic.main.item_favorite.view.*

class FavoritesAdapter internal constructor(private val favorites: List<Place>,
                                               private val listener: OnItemClickListener)
    : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(favorites[position])
    }

    override fun getItemCount() = favorites.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Place) {

            itemView.streetText.text = item.getFormattedStreet()
            itemView.setOnClickListener { listener.onItemClick(item) }
        }
    }

    internal interface OnItemClickListener {
        fun onItemClick(favorite: Place)
    }
}