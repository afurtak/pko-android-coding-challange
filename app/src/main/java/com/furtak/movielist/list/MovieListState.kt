package com.furtak.movielist.list

import com.furtak.movielist.model.Movie

data class MovieListState(
    val movies: List<Movie> = emptyList(),
    val thereAreMoreMoviesToLoad: Boolean = true,
)