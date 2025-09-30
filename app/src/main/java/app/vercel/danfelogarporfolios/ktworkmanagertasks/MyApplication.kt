package app.vercel.danfelogarporfolios.ktworkmanagertasks

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.CustomWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.DemoApi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: CustomWorkerFactory

    // Change this to a property
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
}

class CustomWorkerFactory @Inject constructor(private val api: DemoApi): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = CustomWorker(api, appContext, workerParameters)
}
