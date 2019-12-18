package com.videodemoapplication

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.videodemoapplication.adapter.Ad_Movies
import com.videodemoapplication.api.MovesApi
import com.videodemoapplication.interfaces.OnLoadMoreListener
import com.videodemoapplication.model.MovieResult
import com.videodemoapplication.model.Movies
import com.videodemoapplication.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), Ad_Movies.Listener {

    companion object {
        private const val TAG = "MainActivity"
        var pageNumber: Int? = 1
    }

    private var myAdapter: Ad_Movies? = null
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private var myCompositeDisposable: CompositeDisposable? = null
    private var myRetroMovieArrayList: ArrayList<MovieResult?> = ArrayList()
    private var connectivityDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myCompositeDisposable = CompositeDisposable()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                Log.d(TAG, connectivity.toString())
                if (connectivity.available()) {
                    pageNumber = 1
                    connectivity_status.visibility = View.GONE
                    movie_list.visibility = View.VISIBLE
                    loadData()
                } else {
                    connectivity_status.visibility = View.VISIBLE
                    movie_list.visibility = View.GONE
                    movie_list.adapter = null
                    connectivity_status.text = "No connection"
                    pageNumber = 1
                }
            }
    }

    private fun initRecyclerView() {
        //Use a layout manager to position your items to look like a Grid ListView//
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        movie_list.layoutManager = layoutManager
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        movie_list.addItemDecoration(
            SpacesItemDecoration(
                spacingInPixels
            )
        )

        scrollListener = RecyclerViewLoadMoreScroll(layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                pageNumber = pageNumber!!.inc()
                LoadMoreData(pageNumber!!)
            }
        })

        movie_list.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData(number: Int) {
        //Add the Loading View
        myAdapter!!.addLoadingView()

        //Define the Retrofit request//
        val requestInterface = Retrofit.Builder()

            //Set the API’s base URL//
            .baseUrl(BASE_URL)

            //Specify the converter factory to use for serialization and deserialization//
            .addConverterFactory(GsonConverterFactory.create())

            //Add a call adapter factory to support RxJava return types//
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            //Build the Retrofit instance//
            .build().create(MovesApi::class.java)

        //Add all RxJava disposables to a CompositeDisposable//
        myCompositeDisposable?.add(
            requestInterface.getNextPages(number)
                //Send the Observable’s notifications to the main UI thread//
                .observeOn(AndroidSchedulers.mainThread())

                //Subscribe to the Observer away from the main UI thread//
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseMore)
        )

    }

    private fun handleResponseMore(movies: Movies) {
        Handler().postDelayed({
            //Remove the Loading View
            myAdapter!!.removeLoadingView()
            //We adding the data to our main ArrayList
            myAdapter!!.addData(movies.results)
            //Change the boolean isLoading to false
            scrollListener.setLoaded()
            //Update the recyclerView in the main thread
            movie_list.post {
                myAdapter!!.notifyDataSetChanged()
            }
        }, 1500)
    }


    //Implement loadData//

    private fun loadData() {
        progress_circular.visibility = View.VISIBLE
        //Define the Retrofit request//

        val requestInterface = Retrofit.Builder()

            //Set the API’s base URL//
            .baseUrl(BASE_URL)

            //Specify the converter factory to use for serialization and deserialization//
            .addConverterFactory(GsonConverterFactory.create())

            //Add a call adapter factory to support RxJava return types//
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            //Build the Retrofit instance//
            .build().create(MovesApi::class.java)

        //Add all RxJava disposables to a CompositeDisposable//
        myCompositeDisposable?.add(
            requestInterface.getNextPages(1)

                //Send the Observable’s notifications to the main UI thread//
                .observeOn(AndroidSchedulers.mainThread())

                //Subscribe to the Observer away from the main UI thread//
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse)
        )

    }


    private fun handleResponse(movies: Movies) {
        progress_circular.visibility = View.GONE
        myRetroMovieArrayList = movies.results
        myAdapter = Ad_Movies(
            myRetroMovieArrayList,
            this,
            this
        )
        movie_list.adapter = myAdapter

    }


    override fun onItemClick(movie: MovieResult) {
        Toast.makeText(this, " popularity: ${movie.popularity}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        //Clear all your disposables//
        myCompositeDisposable?.clear()

    }

}
