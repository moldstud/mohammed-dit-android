package com.videodemoapplication.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.videodemoapplication.model.MovieResult
import com.videodemoapplication.R
import com.videodemoapplication.utils.VIEW_TYPE_ITEM
import com.videodemoapplication.utils.VIEW_TYPE_LOADING
import kotlinx.android.synthetic.main.row_movies.view.*

class Ad_Movies(

    private val movieList: ArrayList<MovieResult?>,
    private val listener:
    //Extend RecyclerView.Adapter//
    Listener,
    private val context: Context

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface Listener {
        fun onItemClick(movie: MovieResult)
    }


    fun addData(moviesList: ArrayList<MovieResult?>) {
        this.movieList.addAll(moviesList)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //Add loading item
        Handler().post {
            movieList.add(null)
            notifyItemInserted(movieList.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (movieList.size != 0) {
            movieList.removeAt(movieList.size - 1)
            notifyItemRemoved(movieList.size)
        }
    }


    //Check how many items you have to display//
    override fun getItemCount(): Int = movieList.count()

    override fun getItemViewType(position: Int): Int {
        return if (movieList[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_movies, parent, false)
            itemHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            LoadingViewHolder(view)
        }

    }

    //Bind the ViewHolder//
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Pass the position where each item should be displayed//
        when (holder) {
            is itemHolder ->  holder.bind(movieList[position]!!, listener, context)
            is LoadingViewHolder -> holder.bind()
            else -> throw IllegalArgumentException()
        }

    }

    //Create a ViewHolder class for your RecyclerView items//

    class itemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Assign values from the data model, to their corresponding Views//
        fun bind(
            movie: MovieResult,
            listener: Listener,
            context: Context
        ) {
            itemView.setOnClickListener { listener.onItemClick(movie) }
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
                .into(itemView.iv_cover)
            itemView.tx_name.text = movie.title
            itemView.tx_date.text = movie.release_date
            itemView.vote_average.text = "Vote Average: ${movie.vote_average} "
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {

        }
    }


}


