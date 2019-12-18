package com.videodemoapplication.model

import com.google.gson.annotations.SerializedName

data class Movies(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: ArrayList<MovieResult?>
)

data class MovieResult(
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("vote_count") val vote_count: Int,
    @SerializedName("video") val video: Boolean,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("id") val id: String,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("backdrop_path") val backdrop_path: String,
    @SerializedName("original_language") val original_language: String,
    @SerializedName("original_title") val original_title: String,
    @SerializedName("genre_ids") val genre_ids: List<Int>,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val vote_average: Double,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val release_date: String
)