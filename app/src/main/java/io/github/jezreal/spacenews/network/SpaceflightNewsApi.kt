package io.github.jezreal.spacenews.network

import io.github.jezreal.spacenews.models.Article
import retrofit2.Response
import retrofit2.http.GET

interface SpaceflightNewsApi {

    @GET("api/v2/articles/")
    suspend fun getArticleList(): Response<List<Article>>
}