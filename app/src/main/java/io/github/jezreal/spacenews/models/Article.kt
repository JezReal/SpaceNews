package io.github.jezreal.spacenews.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Article(
    @Json(name = "events")
    val events: List<Any>,
    @Json(name = "featured")
    val featured: Boolean,
    @Json(name = "id")
    val id: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
    @Json(name = "launches")
    val launches: List<Any>,
    @Json(name = "newsSite")
    val newsSite: String,
    @Json(name = "publishedAt")
    val publishedAt: String,
    @Json(name = "summary")
    val summary: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "url")
    val url: String
)