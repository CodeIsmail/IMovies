package dev.codeismail.imovies.ui.popularmoviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.codeismail.imovies.BuildConfig
import dev.codeismail.imovies.R
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.databinding.FragmentPopularMovieDetailBinding
import dev.codeismail.imovies.ui.popularmovies.PopularMoviesViewModel
import dev.codeismail.imovies.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularMovieDetailFragment : Fragment() {

    private var _binding: FragmentPopularMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PopularMoviesViewModel by viewModels()
    private val args: PopularMovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPopularMovieDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMovieById(args.movieId)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.movieDetailUIState.collectLatest {movieDetailState ->
                    if (!movieDetailState.isLoading){
                        binding.progressBar.gone()
                        binding.mainView.show()
                        populateViews(movieDetailState.movie)
                    }else{
                        binding.progressBar.show()
                        binding.mainView.gone()
                    }
                }
            }
        }
    }

    private fun populateViews(movie: Movie?) {
        if (movie != null){
            binding.movieTitleTV.text = movie.title
            binding.overviewDetailTV.text = if (movie.overview.isEmpty()){
                "N/A"
            }else{
                movie.overview
            }
            binding.releaseDateTV.text = movie.releaseDate
            binding.voteAverageTV.text = movie.voteAverage.toString()
            binding.posterIV.load(BuildConfig.MOVIE_POSTER_BASEURL + WIDTH185_URL + movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_place_holder)
            }
            if (movie.backdropUrl != "N/A"){
                binding.backdropImageView.load(BuildConfig.MOVIE_POSTER_BASEURL + WIDTH500_URL + movie.backdropUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_place_holder)
                }
            }

        }else{
            Snackbar.make(binding.mainView,
                getString(R.string.error_message_movie_detail),
                Snackbar.LENGTH_LONG).show()
            navigateBack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}