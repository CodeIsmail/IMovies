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
    private val movieRepo: IMoviesRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PopularMoviesUIState())
    val uiState: StateFlow<PopularMoviesUIState> = _uiState.asStateFlow()



    fun actionGetMovies() {
        val response = movieRepo.getPopularMovies().cachedIn(viewModelScope)
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            response.catch {
                _uiState.update { _errorState ->
                    _errorState.copy(
                        userMessages = _errorState.userMessages
                                + Message(
                            UUID.randomUUID().mostSignificantBits,
                            it.message ?: "Error has occurred"
                        ), isLoading = false
                    )
                }
            }
                .collect {
                    _uiState.update { _successState ->
                        _successState.copy(movies = it, isLoading = false)
                    }
                }
        }
    }

    fun updateUserMessage(id: Long) {
        _uiState.update { currentUiState ->
            val messages = currentUiState.userMessages.filterNot { it.id == id }
            currentUiState.copy(userMessages = messages)
        }
    }

}