package dev.codeismail.imovies.ui.popularmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.data.repositories.IMoviesRepository
import dev.codeismail.imovies.util.Message
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    movieRepo: IMoviesRepository
): ViewModel() {

    val popularMovies = movieRepo.getPopularMovies().cachedIn(viewModelScope)


}