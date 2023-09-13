package com.furtak.movielist.data.http

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Test

class MoviesServiceTest {

    private val server = MockWebServer()

    private val movieService = retrofit.newBuilder()
        .baseUrl(server.url("/")).build()
        .create(MoviesRemoteService::class.java)

    @Test
    fun test() = runTest {
        server.use { server ->
            // given
            server.enqueue(MockResponse().setBody(response))

            // when
            val movies = movieService.getMovies(1)

            // then
            Assert.assertEquals(1, movies.page)
        }
    }
}

val response = """
    {
      "dates": {
        "maximum": "2023-09-10",
        "minimum": "2023-07-24"
      },
      "page": 1,
      "results": [
        {
          "adult": false,
          "backdrop_path": "/8pjWz2lt29KyVGoq1mXYu6Br7dE.jpg",
          "genre_ids": [
            28,
            878,
            27
          ],
          "id": 615656,
          "original_language": "en",
          "original_title": "Meg 2: The Trench",
          "overview": "An exploratory dive into the deepest depths of the ocean of a daring research team spirals into chaos when a malevolent mining operation threatens their mission and forces them into a high-stakes battle for survival.",
          "popularity": 3930.369,
          "poster_path": "/4m1Au3YkjqsxF8iwQy0fPYSxE0h.jpg",
          "release_date": "2023-08-02",
          "title": "Meg 2: The Trench",
          "video": false,
          "vote_average": 7,
          "vote_count": 1637
        }
      ],
      "total_pages": 95,
      "total_results": 1896
    }
""".trimIndent()