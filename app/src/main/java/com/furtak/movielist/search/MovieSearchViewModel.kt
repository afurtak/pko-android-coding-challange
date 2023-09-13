package com.furtak.movielist.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furtak.movielist.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieSearchViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
) : ViewModel() {
    private var page = 1

    private val _movies: MutableStateFlow<MovieSearchListState> = MutableStateFlow(MovieSearchListState())
    val movies: Flow<MovieSearchListState> = _movies

    private val _errorOccurred: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorOccurred: Flow<Boolean> = _errorOccurred

    val favorites: Flow<Set<Int>> = movieRepository.getFavoritesFlow()

    fun search(searchPhrase: String) {
        page = 1
        _movies.value = MovieSearchListState(searchPhrase = searchPhrase)
        searchMoreMovies()
    }

    fun searchMoreMovies() = viewModelScope.launch(Dispatchers.IO) {
        _movies.update { state -> tryToLoadMoreMovies(state) }
    }

    fun toggleFavorites(movieId: Int) = viewModelScope.launch(Dispatchers.IO) {
        movieRepository.toggleFavorites(movieId)
    }

    fun clearSearchResults() {
        _movies.value = MovieSearchListState()
    }

    fun refresh() {
        clearError()
        searchMoreMovies()
    }

    private fun clearError() {
        _errorOccurred.value = false
    }

    private suspend fun tryToLoadMoreMovies(state: MovieSearchListState = MovieSearchListState()): MovieSearchListState = try {
        val (movies, searchPhrase, thereAreMoreMoviesToLoad) = state

        if (thereAreMoreMoviesToLoad) {
            val (newMovies, lastPage) = movieRepository.searchMovies(page, searchPhrase)
            page++
            clearError()
            MovieSearchListState(
                movies = movies + newMovies,
                searchPhrase = searchPhrase,
                thereAreMoreMoviesToLoad = !lastPage,
            )
        } else {
            MovieSearchListState(
                movies = movies,
                searchPhrase = searchPhrase,
                thereAreMoreMoviesToLoad = false,
            )
        }
    } catch (e: Exception) {
        _errorOccurred.value = true
        state
    }
}