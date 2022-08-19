package dev.codeismail.imovies.ui.popularmovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.codeismail.imovies.BuildConfig
import dev.codeismail.imovies.R
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.databinding.LayoutMovieItemBinding
import dev.codeismail.imovies.util.hide
import dev.codeismail.imovies.util.show

class PopularMoviesAdapter (diffCallback: DiffUtil.ItemCallback<Movie>) :
PagingDataAdapter<Movie, PopularMoviesAdapter.MovieViewHolder>(diffCallback) {

    private var onItemClickListener: ((position: Int) -> Unit)? = null
    private var onItemLongClickListener: ((position: Int) -> Boolean)? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let {  item->
            holder.bind(item)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bnd = LayoutMovieItemBinding.inflate(layoutInflater, parent, false)

        return MovieViewHolder(bnd, onItemClickListener, onItemLongClickListener)
    }

    fun setOnItemClickListener(onItemClickListener: (position: Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: (position: Int) -> Boolean) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    class MovieViewHolder(private val bnd: LayoutMovieItemBinding,
                          private val onItemClickListener: ((position: Int) -> Unit)?,
                          private val onItemLongClickListener: ((position: Int) -> Boolean)?
    ) :
        RecyclerView.ViewHolder(bnd.root) {

        init {
            bnd.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(position)
                }
            }

            bnd.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClickListener?.invoke(position)!!
                }else{
                    false
                }
            }
        }


        fun bind(movie: Movie) {
            bnd.posterImageView.load(BuildConfig.MOVIE_POSTER_BASEURL + movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_place_holder)
            }
            if (movie.voteAverage >= 8.0){
                bnd.voteAveImageView.show()
            }else{
                bnd.voteAveImageView.hide()
            }
        }

    }

    object MovieComparator : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

}