package dev.codeismail.imovies.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.codeismail.imovies.BuildConfig
import dev.codeismail.imovies.data.AppDatabase
import dev.codeismail.imovies.data.AuthInterceptor
import dev.codeismail.imovies.data.PopularMoviesService
import dev.codeismail.imovies.data.repositories.IMoviesRepository
import dev.codeismail.imovies.data.repositories.IMoviesRepositoryImpl
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceComponentModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getInstance(appContext)!!
    }

    //Remote module
    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL).build()
    }

    @Suppress("ConstantConditionIf")
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)

        okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).addInterceptor(authInterceptor)
        return okHttpClientBuilder
            .build()
    }

    @Provides
    fun provideAuthInterceptor(@ApplicationContext appContext: Context): AuthInterceptor {
        return AuthInterceptor(appContext)
    }

    @Provides
    fun providePopularMoviesService(retrofitClient: Retrofit): PopularMoviesService {
        return retrofitClient.create(PopularMoviesService::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class MovieModule{

    @Binds
    abstract fun bindIMovieRepository(
        analyticsServiceImpl: IMoviesRepositoryImpl
    ): IMoviesRepository

}

