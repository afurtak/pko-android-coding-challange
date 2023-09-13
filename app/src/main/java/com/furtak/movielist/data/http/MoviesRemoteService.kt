package com.furtak.movielist.data.http

import com.furtak.movielist.data.http.dto.MovieListPage
import com.furtak.movielist.data.http.dto.MovieDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesRemoteService {
    @GET("movie/now_playing")
    suspend fun getMovies(@Query("page") page: Int): MovieListPage

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetails
}