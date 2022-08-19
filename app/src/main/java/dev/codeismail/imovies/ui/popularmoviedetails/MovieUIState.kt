package dev.codeismail.imovies.ui.popularmoviedetails

import dev.codeismail.imovies.data.models.Movie

data class MovieUIState(
    val movie: Movie? = null,
    val isLoading: Boolean = false
)
