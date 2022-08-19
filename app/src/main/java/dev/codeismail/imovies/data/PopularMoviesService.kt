package dev.codeismail.imovies.data

import dev.codeismail.imovies.data.models.ApiSuccessResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PopularMoviesService {

    @GET("movie/popular/")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): Response<ApiSuccessResponse>
}