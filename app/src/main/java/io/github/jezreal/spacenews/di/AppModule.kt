package io.github.jezreal.spacenews.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jezreal.spacenews.network.SpaceflightNewsApi
import io.github.jezreal.spacenews.repository.ArticleRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://spaceflightnewsapi.net/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSpaceflightNewsApi(): SpaceflightNewsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpaceflightNewsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideArticleRepository(api: SpaceflightNewsApi): ArticleRepository =
        ArticleRepository(api)
}