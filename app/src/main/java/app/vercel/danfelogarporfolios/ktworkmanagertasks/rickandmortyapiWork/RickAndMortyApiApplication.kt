package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RickAndMortyApiApplication : Application(), Configuration.Provider {

    // Inject the HiltWorkerFactory
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Provide the custom WorkManager configuration
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
