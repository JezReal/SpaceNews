package io.github.jezreal.spacenews.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseArticle::class],
    version = 1,
    exportSchema = false
)
abstract class ArticleDatabase : RoomDatabase() {
    abstract val articleDao: ArticleDao
}