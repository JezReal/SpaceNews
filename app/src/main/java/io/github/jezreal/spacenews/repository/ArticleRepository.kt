package io.github.jezreal.spacenews.repository

import io.github.jezreal.spacenews.models.Article
import io.github.jezreal.spacenews.network.SpaceflightNewsApi
import io.github.jezreal.spacenews.wrappers.Resource
import java.net.UnknownHostException
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val api: SpaceflightNewsApi
) {

    suspend fun getArticleList(): Resource<List<Article>> {
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
        }

        catch (e: Exception) {
            Resource.Error(e.message!!)
        }
    }
}