package com.furtak.movielist.data

import com.furtak.movielist.data.database.AppDatabase
import com.furtak.movielist.data.database.MovieDao
import com.furtak.movielist.data.http.MoviesRemoteService
import com.furtak.movielist.data.http.retrofit
import com.furtak.movielist.model.Movie
import com.furtak.movielist.model.MovieDetails
import com.furtak.movielist.model.MoviesPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val moviesRemoteService: MoviesRemoteService = retrofit.create(MoviesRemoteService::class.java),
    private val moviesDao: MovieDao = AppDatabase.getInstance().movieDao(),
) {
    fun getFavoritesFlow(): Flow<Set<Int>> = moviesDao.getFavorites().map(List<Int>::toSet)

    suspend fun getMovies(page: Int): MoviesPage {
        val response = moviesRemoteService.getMovies(page)

        val movies = response.results.map { dto ->
            Movie(
                id = dto.id,
                title = dto.title,
                thumbnailDownloadPath = THUMBNAIL_DOWNLOAD_PATH_TEMPLATE.format(dto.posterPath ?: dto.backdropPath),
            )
        }

        return MoviesPage(
            results = movies,
            lastPage = response.page == response.totalPages,
        )
    }

    suspend fun getMovieDetails(movieId: Int): Flow<MovieDetails> {
        val dto = moviesRemoteService.getMovieDetails(movieId)

        return getFavoritesFlow().map { favorites ->
            MovieDetails(
                id = dto.id,
                title = dto.title,
                tagline = dto.tagline,
                overview = dto.overview,
                posterDownloadPath = POSTER_DOWNLOAD_PATH_TEMPLATE.format(dto.posterPath ?: dto.backdropPath),
                favorite = dto.id in favorites,
                releaseDate = dto.releaseDate,
                rating = dto.voteAverage,
                voteCount = dto.voteCount,
            )
        }
    }

    suspend fun toggleFavorites(movieId: Int) {
        moviesDao.toggleFavorites(movieId)
    }

    companion object {
        private const val THUMBNAIL_DOWNLOAD_PATH_TEMPLATE = "https://image.tmdb.org/t/p/w45/%s"
        private const val POSTER_DOWNLOAD_PATH_TEMPLATE = "https://image.tmdb.org/t/p/w500/%s"
    }
}
