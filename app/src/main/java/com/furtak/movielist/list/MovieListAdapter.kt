package com.furtak.movielist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.furtak.movielist.R
import com.furtak.movielist.databinding.MovieListElementBinding
import com.furtak.movielist.model.Movie
import com.squareup.picasso.Picasso

class MovieListAdapter(
    private var movies: List<Movie>,
    private var favorites: Set<Int>,
    private val goToDetails: (Int) -> Unit,
    private val toggleFavorites: (Int) -> Unit,
) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    @Synchronized
    fun invalidateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    @Synchronized
    fun updateFavorites(newFavorites: Set<Int>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<MovieListElementBinding>(
            /* inflater = */ inflater,
            /* layoutId = */ R.layout.movie_list_element,
            /* parent = */ parent,
            /* attachToParent = */ false
        )

        return MovieViewHolder(binding, goToDetails, toggleFavorites)
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int,
    ) = holder.onBind(movies[position], movies[position].id in favorites)

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(
        private val binding: MovieListElementBinding,
        private val goToDetails: (Int) -> Unit,
        private val toggleFavorites: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: Movie, favorite: Boolean) {
            Picasso.get()
                .load(movie.thumbnailDownloadPath)
                .into(binding.thumbnail)

            val favoriteIcon = if (favorite) {
                R.drawable.baseline_favorite_24
            } else {
                R.drawable.baseline_favorite_border_24
            }

            binding.favorite.setImageResource(favoriteIcon)
            binding.title.text = movie.title

            binding.root.setOnClickListener {
                goToDetails(movie.id)
            }

            binding.favorite.setOnClickListener {
                toggleFavorites(movie.id)
            }
        }
    }

}
