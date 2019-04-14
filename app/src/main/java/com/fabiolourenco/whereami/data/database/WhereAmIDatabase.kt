package com.fabiolourenco.whereami.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fabiolourenco.whereami.data.database.dao.PlaceDao
import com.fabiolourenco.whereami.data.model.database.Place

@Database(entities = [Place::class], version = 1)
abstract class WhereAmIDatabase: RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        private const val DATABASE_NAME = "where-am-i-db"

        private var instance: WhereAmIDatabase? = null

        /**
         * Create Database as singleton to prevent having multiple instances of the database opened
         * at the same time
         */
        fun getInstance(context: Context): WhereAmIDatabase {
            if (instance == null) {
                synchronized(WhereAmIDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context,
                            WhereAmIDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            // By now instance is not null
            return instance!!
        }
    }
}