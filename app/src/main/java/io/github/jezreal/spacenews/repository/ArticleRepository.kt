package io.github.jezreal.spacenews.repository

import io.github.jezreal.spacenews.database.ArticleDatabase
import io.github.jezreal.spacenews.network.NetworkArticle
import io.github.jezreal.spacenews.network.SpaceflightNewsApi
import io.github.jezreal.spacenews.network.toDatabaseModel
import io.github.jezreal.spacenews.wrappers.Resource
import java.net.UnknownHostException
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val api: SpaceflightNewsApi,
    private val database: ArticleDatabase
) {

    val articles = database.articleDao.getArticles()

    suspend fun getArticleList(): Resource<List<NetworkArticle>> {
        return try {
            val response = api.getArticleList()
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: UnknownHostException) {
            Resource.Error("No internet")
        } catch (e: Exception) {
            Resource.Error(e.message!!)
        }
    }

    suspend fun insertArticlesToDatabase(articles: List<NetworkArticle>) {
        database.articleDao.insertArticles(articles.toDatabaseModel())
    }

    suspend fun deleteAllCachedArticles() {
        database.articleDao.deleteAll()
    }
}