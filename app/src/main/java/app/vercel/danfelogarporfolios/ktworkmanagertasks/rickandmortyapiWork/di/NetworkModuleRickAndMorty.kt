package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.di

import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.dao.CharacterDao
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.network.RickMortyApiService
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository.CharactersRepository
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository.NetworkCharactersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleRickAndMorty {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    // Define un qualifier específico
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RickAndMortyOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RickAndMortyRetrofit

    @Provides
    @RickAndMortyOkHttpClient
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .build()

    @Provides
    @RickAndMortyRetrofit
    fun provideRetrofitRickAndMortyApi(
        @RickAndMortyOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRickMortyApiService(
        @RickAndMortyRetrofit retrofit: Retrofit
    ): RickMortyApiService = retrofit.create(RickMortyApiService::class.java)

    @Provides
    @Singleton
    fun provideCharactersRepository(
        rickMortyApiService: RickMortyApiService,
        characterDao: CharacterDao
    ): CharactersRepository {
        return NetworkCharactersRepository(rickMortyApiService, characterDao)
    }
}