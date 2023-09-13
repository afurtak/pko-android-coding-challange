package com.furtak.movielist.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MovieEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun initializeDatabase(context: Context) {
            INSTANCE = Room.databaseBuilder(
                /* context = */ context,
                /* databaseClass = */ AppDatabase::class.java,
                /* name = */ DATABASE_NAME,
            ).build()
        }

        fun getInstance(): AppDatabase = INSTANCE!!
    }
}
