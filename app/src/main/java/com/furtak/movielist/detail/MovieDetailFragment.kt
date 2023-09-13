package com.furtak.movielist.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.furtak.movielist.R
import com.furtak.movielist.databinding.MovieDetailFragmentBinding
import com.furtak.movielist.model.MovieDetails
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {
    private var _binding: MovieDetailFragmentBinding? = null
    private val binding: MovieDetailFragmentBinding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()

    private val movieId: Int by lazy {
        requireArguments().getInt(MOVIE_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailFragmentBinding.inflate(inflater, container, false)
        setupEdgeToEdgeView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.initialize(movieId)
        setUpObservers()
    }

    private fun setupEdgeToEdgeView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            view.updatePadding(insets.left, 0, insets.right, 0)
            binding.movieDetailFragment.updatePadding(bottom = binding.movieDetailFragment.paddingBottom + insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.movieDetails
                    .collect(::updateView)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.errorOccurred
                    .collect { errorOccurred ->
                        if (errorOccurred) showError()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView(movieDetails: MovieDetails?) {
        if (movieDetails != null) {
            binding.movieDetailFragment.visibility = View.VISIBLE

            Picasso.get()
                .load(movieDetails.posterDownloadPath)
                .into(binding.poster)

            binding.title.text = movieDetails.title
            if (movieDetails.tagline.isNotBlank()) {
                binding.tagline.text = movieDetails.tagline
            } else {
                binding.tagline.visibility = View.GONE
            }
            binding.overview.text = movieDetails.overview
            binding.releaseDate.text = getString(R.string.release_date, movieDetails.releaseDate)
            binding.rating.text = getString(R.string.rating, movieDetails.rating)
            binding.totalVotes.text = getString(R.string.total_votes, movieDetails.voteCount)

            val favoriteDrawable = if (movieDetails.favorite) {
                R.drawable.baseline_favorite_24
            } else {
                R.drawable.baseline_favorite_border_24
            }
            binding.favorite.setImageDrawable(ResourcesCompat.getDrawable(resources, favoriteDrawable, null))
            binding.favorite.setOnClickListener {
                viewModel.toggleFavorite(movieDetails.id)
            }
        } else {
            binding.movieDetailFragment.visibility = View.GONE
        }
    }

    private fun showError() = AlertDialog.Builder(requireContext())
        .setCancelable(false)
        .setTitle(R.string.an_error_occurred_while_loading_the_data)
        .setPositiveButton(R.string.retry) { _, _ ->
            viewModel.refresh(movieId)
        }
        .show()

    companion object {
        const val MOVIE_ID = "movie_id"
    }
}
