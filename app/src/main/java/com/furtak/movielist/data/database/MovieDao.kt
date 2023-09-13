package com.furtak.movielist.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Upsert
    suspend fun upsert(movie: MovieEntity)

    @Query("SELECT id FROM MovieEntity WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<Int>>
    
    @Query("SELECT EXISTS(SELECT * FROM MovieEntity WHERE id = :movieId AND isFavorite = 1)")
    suspend fun isFavorite(movieId: Int): Boolean
    
    @Transaction
    suspend fun toggleFavorites(movieId: Int) {
        val isFavorite = isFavorite(movieId)
        upsert(MovieEntity(movieId, !isFavorite))
    }
}