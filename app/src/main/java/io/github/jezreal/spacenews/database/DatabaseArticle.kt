package io.github.jezreal.spacenews.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.jezreal.spacenews.domain.Article

@Entity(tableName = "articles_table")
data class DatabaseArticle(
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val summary: String,
    val title: String,
    val url: String
)

fun List<DatabaseArticle>.toDomainModel(): List<Article> {
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
