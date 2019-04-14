package com.fabiolourenco.whereami

import android.app.Application
import com.fabiolourenco.whereami.data.DataRepository
import com.fabiolourenco.whereami.data.database.WhereAmIDatabase
import timber.log.Timber

class App : Application() {

    val database: WhereAmIDatabase
        get() = WhereAmIDatabase.getInstance(this)

    val repository: DataRepository
        get() = DataRepository.getInstance(database)


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}