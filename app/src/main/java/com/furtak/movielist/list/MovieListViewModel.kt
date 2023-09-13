package com.furtak.movielist.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furtak.movielist.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
) : ViewModel() {
    private var page = 1

    private val _movies: MutableStateFlow<MovieListState> = MutableStateFlow(MovieListState())
    val movies: Flow<MovieListState> = _movies

    private val _errorOccurred: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorOccurred: Flow<Boolean> = _errorOccurred

    val favorites: Flow<Set<Int>> = movieRepository.getFavoritesFlow()

    init {
        loadMoreMovies()
    }

    fun loadMoreMovies() = viewModelScope.launch(Dispatchers.IO) {
        _movies.update { state -> tryToLoadMoreMovies(state) }
    }

    fun toggleFavorites(movieId: Int) = viewModelScope.launch(Dispatchers.IO) {
        movieRepository.toggleFavorites(movieId)
    }

    fun refresh() {
        clearError()
        loadMoreMovies()
    }

    private fun clearError() {
        _errorOccurred.value = false
    }

    private suspend fun tryToLoadMoreMovies(state: MovieListState = MovieListState()): MovieListState = try {
        val (movies, thereAreMoreMoviesToLoad) = state

        if (thereAreMoreMoviesToLoad) {
            val (newMovies, lastPage) = movieRepository.getMovies(page)
            page++
            clearError()
            MovieListState(
                movies = movies + newMovies,
                thereAreMoreMoviesToLoad = !lastPage,
            )
        } else {
            MovieListState(
                movies = movies,
                thereAreMoreMoviesToLoad = false,
            )
        }
    } catch (e: Exception) {
        _errorOccurred.value = true
        state
    }
}
