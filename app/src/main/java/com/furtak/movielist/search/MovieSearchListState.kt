package com.furtak.movielist.search

import com.furtak.movielist.model.Movie

data class MovieSearchListState(
    val movies: List<Movie> = emptyList(),
    val searchPhrase: String = "",
    val thereAreMoreMoviesToLoad: Boolean = true,
)