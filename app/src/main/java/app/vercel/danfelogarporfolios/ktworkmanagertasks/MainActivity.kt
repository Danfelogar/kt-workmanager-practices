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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
// workManagerDemo
//import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.CustomWorker
// ExpeditedWork
import app.vercel.danfelogarporfolios.ktworkmanagertasks.expeditedWork.CustomWorker
// RepeatableWork
import app.vercel.danfelogarporfolios.ktworkmanagertasks.repeatableWork.RepeatableCustomWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ui.theme.KTWorkManagerTasksTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                    MainScreenDummy(
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