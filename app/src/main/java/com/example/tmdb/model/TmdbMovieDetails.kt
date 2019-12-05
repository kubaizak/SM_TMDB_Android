package com.example.tmdb.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl

data class Genre (

    @SerializedName("id: Int")
    var id: Int = 0,

    @SerializedName("name")
    var name: String? = null
)

data class ProductionCompany (

    @SerializedName("id: Int")
    var id: Int = 0,

    @SerializedName("logo_path")
    var logoPath: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("origin_country")
    var originCountry: String? = null
)

data class ProductionCountry (

    @SerializedName("iso_3166_1")
    var iso31661: String? = null,

    @SerializedName("name")
    var name: String? = null
)

data class SpokenLanguage (

    @SerializedName("iso_639_1")
    var iso6391: String? = null,

    @SerializedName("name")
    var name: String? = null
)

data class TmdbMovieDetails (

    @SerializedName("adult")
    var adult:Boolean = false,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("belongs_to_collection")
    var belongsToCollection: Any? = null,

    @SerializedName("budget")
    var budget:Int = 0,

    @SerializedName("genres")
    var genres: List<Genre>? = null,

    @SerializedName("homepage")
    var homepage: String? = null,

    @SerializedName("id: Int")
    var id: Int = 0,

    @SerializedName("imdb_id: Int")
    var imdbid: String? = null,

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("original_title")
    var originalTitle: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("popularity")
    var popularity: Float = 0f,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("production_companies")
    var productionCompanies: List<ProductionCompany>? = null,

    @SerializedName("production_countries")
    var productionCountries: List<ProductionCountry>? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("revenue")
    var revenue: Float = 0f,

    @SerializedName("runtime")
    var runtime: Int = 0,

    @SerializedName("spoken_languages")
    var spokenLanguages: List<SpokenLanguage>? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("tagline")
    var tagline: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("vid: Inteo")
    var video: Boolean = false,

    @SerializedName("vote_average")
    var voteAverage: Float = 0f,

    @SerializedName("vote_count")
    var voteCount: Int = 0,

    var posterFullPath: HttpUrl? = null
)