package app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: DemoApi,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            val response = api.getPost()
            if( response.isSuccessful){
                Log.d("CustomWorker", "Success")
                Log.d("CustomWorker", "Response: ${response.body()}")
                Result.success()
            } else {
                Log.d("CustomWorker", "Error, try to retrying: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.d("CustomWorker", "Exception: ${e.message}")
            return Result.failure(Data.Builder().putString("error", e.message).build())
        }
    }
}