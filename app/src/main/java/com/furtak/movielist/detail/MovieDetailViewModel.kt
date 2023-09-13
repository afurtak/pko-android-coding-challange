package com.furtak.movielist.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furtak.movielist.data.MovieRepository
import com.furtak.movielist.model.MovieDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
) : ViewModel() {

    private val _errorOccurred: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorOccurred: Flow<Boolean> = _errorOccurred

    private val movieIdFlow = MutableStateFlow<Int?>(null)
    val movieDetails: Flow<MovieDetails?> = movieIdFlow.transform { id ->
        try {
            if (id != null) {
                emit(movieRepository.getMovieDetails(id))
            } else {
                emit(flowOf(null))
            }
        } catch(e: Exception) {
            movieIdFlow.value = null
            _errorOccurred.value = true
        }
    }.flattenMerge().onEach { _errorOccurred.value = false }

    fun initialize(movieId: Int) = viewModelScope.launch(Dispatchers.IO) {
        movieIdFlow.value = movieId
    }

    fun toggleFavorite(movieId: Int) = viewModelScope.launch(Dispatchers.IO) {
        movieRepository.toggleFavorites(movieId)
    }

    fun refresh(movieId: Int) = viewModelScope.launch(Dispatchers.IO) {
        movieIdFlow.value = movieId
    }
}
