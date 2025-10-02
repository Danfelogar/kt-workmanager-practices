package app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.vercel.danfelogarporfolios.ktworkmanagertasks.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random

class DownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DownloadWorker", "Starting download work")

        setForeground(createForegroundInfo())
        delay(1000L)

        Log.d("DownloadWorker", "Making API call")

        return try {
            val response = FileApi.instance.downloadImage()

            Log.d("DownloadWorker", "Response code: ${response.code()}")
            Log.d("DownloadWorker", "Response isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("DownloadWorker", "Response body received")
                    saveImageToFile(body)
                } else {
                    Log.e("DownloadWorker", "Response body is null")
                    Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Response body is null"))
                }
            } else {
                Log.e("DownloadWorker", "Network error: ${response.code()} ${response.message()}")
                if (response.code().toString().startsWith("5")) {
                    Result.retry()
                } else {
                    Result.failure(
                        workDataOf(
                            WorkerParams.ERROR_MSG to "Network Error: ${response.code()} ${response.message()}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadWorker", "Exception: ${e.message}", e)
            Result.failure(workDataOf((WorkerParams.ERROR_MSG to e.message ?: "Unknown exception") as Pair<String, Any?>))
        }
    }

    private suspend fun saveImageToFile(body: ResponseBody): Result {
        return withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                val file = File(context.cacheDir, "downloaded_image.jpg")
                Log.d("DownloadWorker", "Saving to file: ${file.absolutePath}")

                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                var totalBytes = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead
                }

                outputStream.flush()
                Log.d("DownloadWorker", "File saved successfully. Size: $totalBytes bytes")

                // Verify that the file exists and has content
                if (file.exists() && file.length() > 0) {
                    Log.d("DownloadWorker", "File verification: exists=${file.exists()}, size=${file.length()}")
                    Result.success(
                        workDataOf(
                            WorkerParams.IMAGE_URI to file.toUri().toString()
                        )
                    )
                } else {
                    Log.e("DownloadWorker", "File verification failed: exists=${file.exists()}, size=${file.length()}")
                    Result.failure(workDataOf(WorkerParams.ERROR_MSG to "File save verification failed"))
                }

            } catch (e: Exception) {
                Log.e("DownloadWorker", "Error saving file: ${e.message}", e)
                Result.failure(workDataOf(WorkerParams.ERROR_MSG to "File save error: ${e.message}"))
            } finally {
                try {
                    inputStream?.close()
                    outputStream?.close()
                } catch (e: Exception) {
                    Log.e("DownloadWorker", "Error closing streams: ${e.message}")
                }
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                Random.nextInt(),
                createNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                Random.nextInt(),
                createNotification()
            )
        }
    }

    private fun createNotification(): Notification {
        val channelId = "download_channel"
        val channelName = "File Download"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Downloading File...")
            .setContentTitle("Download in progress")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        return builder.build()
    }
}