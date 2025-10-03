package app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil3.compose.AsyncImage

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultipleWorkersScreen(
    modifier: Modifier = Modifier,
    applicationContext: Context
) {
    val permission = rememberPermissionState(POST_NOTIFICATIONS)
    val workManager = WorkManager.getInstance(applicationContext)

    // Observe the state of the entire unique work chain
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("download")
        .observeAsState()
        .value

    val imageFile by remember(workInfos) {
        derivedStateOf {
            // Helper function to convert URI string to File
            fun uriStringToFile(uriString: String?): File? {
                if (uriString.isNullOrEmpty()) return null
                return try {
                    val filePath = if (uriString.startsWith("file://")) {
                        uriString.removePrefix("file://")
                    } else {
                        uriString
                    }
                    File(filePath)
                } catch (e: Exception) {
                    null
                }
            }

            // Search through all workers in the chain
            workInfos?.forEach { info ->
                Log.d("MultipleWorkersScreen", "Worker: ${info.tags}, State: ${info.state}")

                // Check if it has output data
                if (info.outputData.keyValueMap.isNotEmpty()) {
                    Log.d("MultipleWorkersScreen", "Output data: ${info.outputData.keyValueMap}")
                }

                // Look for FILTER_URI first (last worker in the chain)
                val filterUri = info.outputData.getString(WorkerParams.FILTER_URI)
                if (filterUri != null && info.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MultipleWorkersScreen", "Found filter URI: $filterUri")
                    return@derivedStateOf uriStringToFile(filterUri)
                }

                // Then look for IMAGE_URI
                val downloadUri = info.outputData.getString(WorkerParams.IMAGE_URI)
                if (downloadUri != null && info.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MultipleWorkersScreen", "Found download URI: $downloadUri")
                    return@derivedStateOf uriStringToFile(downloadUri)
                }
            }
            null
        }
    }
    if(!permission.status.isGranted) {
        Text("Please grant notification permission to start the download task.")
        LaunchedEffect(Unit) {
            permission.launchPermissionRequest()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        imageFile?.let { file ->
            if (file.exists()) {
                Log.d("MultipleWorkersScreen", "🖼️ Displaying image file: ${file.absolutePath}")

                AsyncImage(
                    model = file,
                    contentDescription = "Processed image",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("✅ Image loaded successfully!")
                Text("File: ${file.name}", fontSize = 12.sp)
            } else {
                Text("❌ File does not exist: ${file.absolutePath}")
            }
        } ?: Text("No image to display - Click download button")

        Button(
            onClick = {
                Log.d("MultipleWorkersScreen", "🚀 Starting download + filter chain...")

                val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
                    .build()

                workManager.beginUniqueWork(
                    "download",
                    ExistingWorkPolicy.REPLACE,
                    downloadRequest
                ).then(colorFilterRequest).enqueue()
            },
            enabled = workInfos?.none { it.state == WorkInfo.State.RUNNING } ?: true
        ) {
            Text("Start Download + Filter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display status of all workers
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            workInfos?.forEach { info ->
                Text("${info.tags.lastOrNull()}: ${info.state}")
            }
        }
    }
}