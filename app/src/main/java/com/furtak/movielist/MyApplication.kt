package com.furtak.movielist

import android.app.Application
import com.furtak.movielist.data.database.AppDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.initializeDatabase(this)
    }
}