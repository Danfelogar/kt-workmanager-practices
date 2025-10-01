package app.vercel.danfelogarporfolios.ktworkmanagertasks.repeatableWork

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class RepeatableCustomWorker constructor(
    context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        delay(10000)
        Log.d("RepeatableCustomWorker", "doWork: I'm working...")
        return Result.success()
    }
}