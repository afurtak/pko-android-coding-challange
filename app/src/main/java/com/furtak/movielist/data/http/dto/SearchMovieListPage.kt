package com.furtak.movielist.data.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class SearchMovieListPage(
    val page: Int,
    val results: List<SearchMovie>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int,
)
