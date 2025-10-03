package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.di

import android.content.Context
import androidx.room.Room
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.database.DatabaseRickAndMorty
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val RICK_MORTY_DATABASE = "rick_morty_database"

    @Singleton
    @Provides
    fun provideDatabaseRickAndMorty(@ApplicationContext context: Context): DatabaseRickAndMorty {
        return Room.databaseBuilder(
            context,
            DatabaseRickAndMorty::class.java,
            RICK_MORTY_DATABASE
        ).build()
    }


    @Singleton
    @Provides
    fun provideCharacterDao(db: DatabaseRickAndMorty) = db.characterDao()
}