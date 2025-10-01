package app.vercel.danfelogarporfolios.ktworkmanagertasks.expeditedWork

import app.vercel.danfelogarporfolios.ktworkmanagertasks.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun doWork(): Result {
        try {
            setForeground(getForegroundInfo(applicationContext))
            Log.d("CustomWorker", "Success!???")
            delay(10000)
            return Result.success()
        }catch (e: Exception){
            Log.d("CustomWorker", "Exception: ${e.message}")
            return Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getForegroundInfo(context: Context): ForegroundInfo {
    return ForegroundInfo(
        1,
        createNotification(context = context),
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
    )
}

private fun createNotification(context: Context): Notification {
    val channelId = "main_channel_id"
    val channelName = "Main Channel"

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Notification Title")
        .setContentText("This is my first notification.")
        .setOngoing(true)
        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    return builder.build()
}