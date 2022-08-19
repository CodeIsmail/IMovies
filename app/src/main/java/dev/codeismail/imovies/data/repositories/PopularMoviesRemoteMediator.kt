package dev.codeismail.imovies.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dev.codeismail.imovies.data.AppDatabase
import dev.codeismail.imovies.data.PopularMoviesService
import dev.codeismail.imovies.data.models.Movie
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class PopularMoviesRemoteMediator (
    private val appDatabase: AppDatabase,
    private val popularMovieService: PopularMoviesService)
    : RemoteMediator<Int, Movie>() {

        private val popularMovieDao = appDatabase.movieDao()
        private val remoteKeysDao = appDatabase.remoteKeysDao()

        override suspend fun initialize(): InitializeAction {
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
//            return if (System.currentTimeMillis() - prefsManager.getLong(DB_TIMER) >= cacheTimeout)
//            {
//                InitializeAction.SKIP_INITIAL_REFRESH
//            } else {
//                InitializeAction.LAUNCH_INITIAL_REFRESH
//            }
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }


        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, Movie>
        ): MediatorResult {
            return try {
                val page: Int = when (loadType) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                    }
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        val prevKey = remoteKeys?.prevKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                        prevKey
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        val nextKey = remoteKeys?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                        nextKey
                    }
                }

                val response = popularMovieService.getPopularMovies(page = page)

                if (response.isSuccessful){
                    val remoteMovies = response.body()!!.results
                    val endOfPaginationReached = remoteMovies.isEmpty()
                    appDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            appDatabase.movieDao().clearAll()
                            appDatabase.remoteKeysDao().clearRemoteKeys()
                        }
                        val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val keys = remoteMovies.map {
                            RemoteKeys(movieId = "${it.id}", prevKey = prevKey, nextKey = nextKey)
                        }
                        val champions = remoteMovies.map {movie->

                            Movie("${movie.id}", movie.originalTitle, movie.posterPath, movie.voteAverage)
                        }
                        remoteKeysDao.insertAll(keys)
                        popularMovieDao.insertAll(champions)
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = endOfPaginationReached
                    )
                }else{
                    val code = response.code()
                    MediatorResult.Error(Exception(response.message()))
                }
            } catch (e: IOException) {
                MediatorResult.Error(e)
            } catch (e: HttpException) {
                MediatorResult.Error(e)
            }

        }

        private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
            // Get the last page that was retrieved, that contained items.
            // From that last page, get the last item
            return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { movie ->
                    // Get the remote keys of the last item retrieved
                    appDatabase.remoteKeysDao().remoteKeysChampionId(movie.id)
                }
        }

        private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
            // Get the first page that was retrieved, that contained items.
            // From that first page, get the first item
            return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { movie ->
                    // Get the remote keys of the first items retrieved
                    appDatabase.remoteKeysDao().remoteKeysChampionId(movie.id)
                }
        }

        private suspend fun getRemoteKeyClosestToCurrentPosition(
            state: PagingState<Int, Movie>
        ): RemoteKeys? {
            // The paging library is trying to load data after the anchor position
            // Get the item closest to the anchor position
            return state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    appDatabase.remoteKeysDao().remoteKeysChampionId(id)
                }
            }
        }
    }

