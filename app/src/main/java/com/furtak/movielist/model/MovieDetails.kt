package com.furtak.movielist.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val tagline: String,
    val overview: String,
    val posterDownloadPath: String,
    val favorite: Boolean,
    val releaseDate: String,
    val rating: Double,
    val voteCount: Int,
)