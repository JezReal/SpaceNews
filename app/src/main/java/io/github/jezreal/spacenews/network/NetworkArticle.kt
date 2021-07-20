package io.github.jezreal.spacenews.network

import androidx.lifecycle.Transformations.map
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.jezreal.spacenews.database.DatabaseArticle
import io.github.jezreal.spacenews.domain.Article
import retrofit2.Response

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

fun List<NetworkArticle>.toDomainModel(): List<Article> {
    return map {
        Article(
            id = it.id,
            imageUrl = it.imageUrl,
            summary = it.summary,
            title = it.title,
            url = it.url
        )
    }
}

fun List<NetworkArticle>.toDatabaseModel(): Array<DatabaseArticle> {
    return map {
        DatabaseArticle(
            id = it.id,
            imageUrl = it.imageUrl,
            summary = it.summary,
            title = it.title,
            url = it.url
        )
    }.toTypedArray()
}