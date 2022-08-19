package dev.codeismail.imovies.ui.popularmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dev.codeismail.imovies.R
import dev.codeismail.imovies.databinding.FragmentPopularMoviesBinding
import dev.codeismail.imovies.util.MovieItemDecorator
import dev.codeismail.imovies.util.gone
import dev.codeismail.imovies.util.navigate
import dev.codeismail.imovies.util.show
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularMoviesFragment : Fragment() {

    private var _binding: FragmentPopularMoviesBinding? = null
    private val binding get() = _binding!!

    private val pagingAdapter = PopularMoviesAdapter(PopularMoviesAdapter.MovieComparator)
    private val viewModel: PopularMoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPopularMoviesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moviesRV.apply {
            adapter = pagingAdapter
            setHasFixedSize(true)
            addItemDecoration(MovieItemDecorator(requireContext(), R.dimen.dimen_8dp))
        }

        pagingAdapter.setOnItemClickListener {
            val movie = pagingAdapter.peek(it)!!
            val action = PopularMoviesFragmentDirections
                .actionPopularMoviesFragmentToSecondFragment(movie.id)
            navigate(action)
        }

        pagingAdapter.setOnItemLongClickListener {
            val movie = pagingAdapter.peek(it)!!
            Snackbar.make(binding.moviesRV, movie.title, Snackbar.LENGTH_LONG).show()
            true
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.popularMovies.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                pagingAdapter.loadStateFlow.collectLatest { state ->
                    when (state.refresh) {
                        is LoadState.NotLoading -> {
                            binding.progressBar.gone()
                            binding.moviesRV.show()
                        }
                        is LoadState.Loading -> {
                            binding.moviesRV.gone()
                            binding.progressBar.show()
                        }
                        is LoadState.Error -> {
                            binding.progressBar.gone()
                            binding.moviesRV.show()
                            Snackbar.make(binding.moviesRV, getString(R.string.error_message), Snackbar.LENGTH_LONG).show()
                        }
                    }
                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}