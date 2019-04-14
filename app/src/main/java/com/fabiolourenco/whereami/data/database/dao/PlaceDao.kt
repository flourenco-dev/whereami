package com.fabiolourenco.whereami.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fabiolourenco.whereami.data.model.database.Place

@Dao
interface PlaceDao {

    @Query("SELECT * FROM favorites")
    fun loadFavorites(): LiveData<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(place: Place): Long

    @Delete
    fun deleteFavorite(place: Place)
}