package app.vercel.danfelogarporfolios.ktworkmanagertasks

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import androidx.work.Constraints
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
// workManagerDemo
//import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.CustomWorker
// ExpeditedWork
import app.vercel.danfelogarporfolios.ktworkmanagertasks.expeditedWork.CustomWorker
// RepeatableWork
import app.vercel.danfelogarporfolios.ktworkmanagertasks.repeatableWork.RepeatableCustomWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ui.theme.KTWorkManagerTasksTheme
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide.ColorFilterWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide.DownloadWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide.WorkerParams
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // workManagerDemo
//        val workRequest = OneTimeWorkRequestBuilder<CustomWorker>()
//            .setInitialDelay(Duration.ofSeconds(10))
//            .setBackoffCriteria(
//                backoffPolicy = BackoffPolicy.LINEAR,
//                duration = Duration.ofSeconds(15)
//            )
//            .build()

        enableEdgeToEdge()
        setContent {
            KTWorkManagerTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    MainScreenDummy(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        applicationContext = applicationContext,
//                    )
                    MultipleWorkersScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        applicationContext = applicationContext,
                    )
                }
            }
        }
    }
}

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
                    imageLoader = ImageLoader.Builder(LocalContext.current).build(),
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun MainScreenDummy(
    modifier: Modifier = Modifier,
    applicationContext: Context
) {
    // ExpeditedWork
    val permission = rememberPermissionState(POST_NOTIFICATIONS)
//    if(permission.status.isGranted) {
//        //expeditedWork
//        val workRequest = OneTimeWorkRequestBuilder<CustomWorker>()
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .setBackoffCriteria(
//                backoffPolicy = BackoffPolicy.LINEAR,
//                duration = Duration.ofSeconds(15)
//            )
//            .build()
//
//        WorkManager.getInstance(applicationContext).enqueue(workRequest)
//    }else{
//        LaunchedEffect(Unit) {
//            permission.launchPermissionRequest()
//        }
//    }

    //RepeatableWork
    val lifecycleOwner = LocalLifecycleOwner.current
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .setRequiresCharging(true)
        .build()
    val workRequest = PeriodicWorkRequestBuilder<RepeatableCustomWorker>(
        // repeatInterval: How often the work should repeat (minimum 15 minutes in production)
        repeatInterval = 1,
        // repeatIntervalTimeUnit: The unit for repeatInterval (here, HOURS)
        repeatIntervalTimeUnit = TimeUnit.HOURS,
        // flexTimeInterval: The window in which the work can be executed (minimum 5 minutes)
        flexTimeInterval = 15,
        // flexTimeIntervalUnit: The unit for flexTimeInterval (here, MINUTES)
        flexTimeIntervalUnit = TimeUnit.MINUTES
    ).setBackoffCriteria(
        // backoffPolicy: Strategy for retrying if the work fails (LINEAR or EXPONENTIAL)
        backoffPolicy = BackoffPolicy.LINEAR,
        // duration: How long to wait before retrying after a failure
        duration = Duration.ofSeconds(15)
    ).setConstraints(
        // constraints: Conditions that must be met for the work to run (e.g., network, charging)
        constraints
    ).build()

    val workManager = WorkManager.getInstance(applicationContext)

    LaunchedEffect(Unit) {
        workManager.enqueueUniquePeriodicWork(
            "RepeatableCustomWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        workManager.getWorkInfosForUniqueWorkLiveData("RepeatableCustomWorker")
            .observe(lifecycleOwner) { workInfoList ->
                if (!workInfoList.isNullOrEmpty()) {
                    val workInfo = workInfoList[0]
                    // Log the state of the work
                    Log.d("MainScreenDummy", "Work state: ${workInfo.state.name} " + "Last attempt: ${workInfo.runAttemptCount}")
                }
            }
        delay(35000)
        workManager.cancelUniqueWork("RepeatableCustomWorker")
    }

    Box(modifier = modifier.fillMaxSize()){}
}