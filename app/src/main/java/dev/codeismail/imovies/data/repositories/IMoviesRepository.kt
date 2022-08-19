package dev.codeismail.imovies.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.codeismail.imovies.data.AppDatabase
import dev.codeismail.imovies.data.PopularMoviesService
import dev.codeismail.imovies.data.models.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface IMoviesRepository{
    fun getPopularMovies(): Flow<PagingData<Movie>>
    suspend fun getMovieById(movieId: String): Movie?
}

class IMoviesRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val popularMovieService: PopularMoviesService
): IMoviesRepository{
    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        val pagingSourceFactory = {
            appDatabase.movieDao().getAllMovies()
        }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = true),
            remoteMediator = PopularMoviesRemoteMediator(
                appDatabase,
                popularMovieService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getMovieById(movieId: String) : Movie?{
        return appDatabase.movieDao().getMovieById(movieId)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}

