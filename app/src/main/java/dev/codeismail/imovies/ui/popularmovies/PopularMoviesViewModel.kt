package dev.codeismail.imovies.ui.popularmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.data.repositories.IMoviesRepository
import dev.codeismail.imovies.ui.popularmoviedetails.MovieUIState
import dev.codeismail.imovies.util.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    private val movieRepo: IMoviesRepository
): ViewModel() {

    val popularMovies = movieRepo.getPopularMovies().cachedIn(viewModelScope)

    private val _movieDetailUIState = MutableStateFlow(MovieUIState())
    val movieDetailUIState: StateFlow<MovieUIState> = _movieDetailUIState.asStateFlow()

    fun getMovieById(id: String){
        _movieDetailUIState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _movieDetailUIState.update {
                    it.copy(movie = movieRepo.getMovieById(id), isLoading = false)
                }
            }
        }
    }


}