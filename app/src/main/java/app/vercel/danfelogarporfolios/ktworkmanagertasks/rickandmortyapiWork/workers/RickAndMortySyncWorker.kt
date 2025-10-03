package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.RickAndMortyApiApplication
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository.CharactersRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@HiltWorker
class RickAndMortySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository: CharactersRepository by lazy {
        val app = context.applicationContext as RickAndMortyApiApplication
        // Obtén el repositorio del contenedor de Hilt
        EntryPointAccessors.fromApplication(
            app,
            RepositoryEntryPoint::class.java
        ).repository()
    }

    companion object {
        private const val TAG = "RickAndMortySyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "✅ Synchronizing characters from API...")
            val characters = repository.getCharacters()
            Log.d(TAG, "✅ Worker synchronized: ${characters.size} characters")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in synchronizing the characters", e)
            Result.retry()
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun repository(): CharactersRepository
}

//package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.workers
//
//import android.content.Context
//import android.util.Log
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository.CharactersRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//
//@HiltWorker
//class RickAndMortySyncWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted params: WorkerParameters,
//    private val repository: CharactersRepository
//) : CoroutineWorker(context, params) {
//
//    companion object {
//        private const val TAG = "RickAndMortySyncWorker"
//    }
//
//    override suspend fun doWork(): Result {
//        return try {
//            Log.d(TAG, "✅ Synchronizing characters from API...")
//            val characters = repository.getCharacters()
//            Log.d(TAG, "✅ Worker synchronized: ${characters.size} characters")
//            Result.success()
//        } catch (e: Exception) {
//            Log.d(TAG, "❌ Error in synchronizing the characters: ${e.message}")
//            Result.retry()
//        }
//    }
//}