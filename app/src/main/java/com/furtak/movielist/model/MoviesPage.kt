package com.furtak.movielist.model

data class MoviesPage(
    val results: List<Movie>,
    val lastPage: Boolean,
)