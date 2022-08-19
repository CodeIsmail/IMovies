package dev.codeismail.imovies.ui.popularmovies

import androidx.paging.PagingData
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.util.Message

data class PopularMoviesUIState (
    val movies: PagingData<Movie> = PagingData.empty(),
    val userMessages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)