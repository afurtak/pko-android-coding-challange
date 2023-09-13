package com.furtak.movielist.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furtak.movielist.R
import com.furtak.movielist.databinding.MovieListFragmentBinding
import com.furtak.movielist.detail.MovieDetailFragment
import kotlinx.coroutines.launch

class MovieListFragment : Fragment() {
    private var _binding: MovieListFragmentBinding? = null
    private val binding: MovieListFragmentBinding get() = _binding!!

    private var _movieListAdapter: MovieListAdapter? = null
    private val movieListAdapter: MovieListAdapter get() = _movieListAdapter!!

    private val viewModel: MovieListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MovieListFragmentBinding.inflate(inflater, container, false)

        setupMovieListAdapter()
        setupEdgeToEdgeView()

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_movieListFragment_to_movieSearchFragment)
        }

        binding.movieListRecyclerView.also { movieList ->
            movieList.adapter = movieListAdapter
            movieList.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.movieListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMoreMovies()
                }
            }
        })

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.movies.collect { (movies, _) ->
                    movieListAdapter.invalidateMovies(movies)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.favorites.collect(movieListAdapter::updateFavorites)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.errorOccurred.collect { errorOccurred ->
                    if (errorOccurred) showError()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _movieListAdapter = null
        _binding = null
    }

    private fun setupMovieListAdapter() {
        _movieListAdapter = MovieListAdapter(
            movies = mutableListOf(),
            favorites = setOf(),
            goToDetails = ::navigateToDetails,
            toggleFavorites = ::toggleFavorites,
        )
    }

    private fun setupEdgeToEdgeView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())

            view.updatePadding(insets.left, 0, insets.right, 0)

            val fabLayoutParams = (binding.fab.layoutParams as ConstraintLayout.LayoutParams).also {
                it.bottomMargin = insets.bottom + 32
            }
            binding.fab.layoutParams = fabLayoutParams

            binding.movieListRecyclerView.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom + binding.fab.marginBottom + binding.fab.height,
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun navigateToDetails(movieId: Int) {
        findNavController().navigate(
            R.id.action_movieListFragment_to_movieDetailFragment,
            bundleOf(MovieDetailFragment.MOVIE_ID to movieId),
        )
    }

    private fun toggleFavorites(movieId: Int) {
        viewModel.toggleFavorites(movieId)
    }

    private fun showError() = AlertDialog.Builder(requireContext())
        .setCancelable(false)
        .setTitle(R.string.an_error_occurred_while_loading_the_data)
        .setPositiveButton(R.string.retry) { _, _ -> viewModel.refresh() }
        .show()
}
