package io.github.jezreal.spacenews.domain

data class Article(
    val id: String,
    val imageUrl: String,
    val summary: String,
    val title: String,
    val url: String
)
