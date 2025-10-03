package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.database

import androidx.room.Room
import androidx.room.Database
import android.content.Context
import androidx.room.RoomDatabase
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.CharacterEntity
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.dao.CharacterDao


@Database(entities = [CharacterEntity::class], version = 1, exportSchema = false)
abstract class DatabaseRickAndMorty : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
}