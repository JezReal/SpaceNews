package io.github.jezreal.spacenews.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("select * from articles_table")
    fun getArticles(): Flow<List<DatabaseArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<DatabaseArticle>)

    @Query("delete from articles_table")
    fun deleteAll()
}