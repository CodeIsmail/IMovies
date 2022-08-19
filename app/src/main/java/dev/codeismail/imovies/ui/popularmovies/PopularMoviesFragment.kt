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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dev.codeismail.imovies.databinding.FragmentPopularMoviesBinding
import dev.codeismail.imovies.util.navigate
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

        viewModel.actionGetMovies()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collectLatest {
                    if(!it.isLoading){
//                        bnd.loadingView.gone()
//                        bnd.championsRecyclerView.show()
                        pagingAdapter.submitData(it.movies)
//                        handleError(it.userMessages)
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