package com.videodemoapplication.api

import com.videodemoapplication.model.Movies
import com.videodemoapplication.utils.API_KEY
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface MovesApi {

    @GET("movie/popular?api_key=${API_KEY}=en-US&page=")
    fun getNextPages(@Query("page") id: Int): Observable<Movies>

}
