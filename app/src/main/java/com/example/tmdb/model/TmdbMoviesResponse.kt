package com.example.tmdb.model

import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl

data class TmdbMovie (
    @SerializedName("popularity")
    var popularity: Float = 0f,

    @SerializedName("vote_count")
    var voteCount: Int = 0,

    @SerializedName("video")
    var isVideo: Boolean = false,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("adult")
    var isAdult: Boolean = false,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("original_title")
    var originalTitle: String? = null,

    @SerializedName("genre_ids")
    var genreIds: List<Int>? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("vote_average")
    var voteAverage: Float = 0f,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    var posterFullPath: HttpUrl? = null
)

data class TmdbMoviesResponse (
    @SerializedName("page")
    var page: Int = 0,

    @SerializedName("total_results")
    var totalResults: Int = 0,

    @SerializedName("total_pages")
    var totalPages: Int = 0,

    @SerializedName("results")
    var results: List<TmdbMovie>? = null
)