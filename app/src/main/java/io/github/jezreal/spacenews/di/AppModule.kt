package io.github.jezreal.spacenews.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jezreal.spacenews.database.ArticleDao
import io.github.jezreal.spacenews.database.ArticleDatabase
import io.github.jezreal.spacenews.network.SpaceflightNewsApi
import io.github.jezreal.spacenews.repository.ArticleRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSpaceflightNewsApi(): SpaceflightNewsApi {
        return Retrofit.Builder()
            .baseUrl(SpaceflightNewsApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpaceflightNewsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideArticleDatabase(@ApplicationContext context: Context): ArticleDatabase {
        return Room.databaseBuilder(
            context,
            ArticleDatabase::class.java,
            "article_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideArticleDao(database: ArticleDatabase): ArticleDao {
        return database.articleDao
    }

    @Singleton
    @Provides
    fun provideArticleRepository(api: SpaceflightNewsApi, database: ArticleDatabase): ArticleRepository =
        ArticleRepository(api, database)
}