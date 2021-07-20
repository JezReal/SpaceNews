package io.github.jezreal.spacenews.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.jezreal.spacenews.database.DatabaseArticle

@JsonClass(generateAdapter = true)
data class NetworkArticle(
    @Json(name = "id")
    val id: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
    @Json(name = "summary")
    val summary: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String
)

fun List<NetworkArticle>.toDatabaseModel(): List<DatabaseArticle> {
    return map {
        DatabaseArticle(
            id = it.id,
            imageUrl = it.imageUrl,
            summary = it.summary,
            title = it.title,
            url = it.url
        )
    }
}