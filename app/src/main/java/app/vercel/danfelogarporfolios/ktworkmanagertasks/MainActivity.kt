package app.vercel.danfelogarporfolios.ktworkmanagertasks

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.os.Build
import android.os.Bundle
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
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import app.vercel.danfelogarporfolios.ktworkmanagertasks.expeditedWork.CustomWorker
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ui.theme.KTWorkManagerTasksTheme
// workManagerDemo
//import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.CustomWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration


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
    val permission = rememberPermissionState(POST_NOTIFICATIONS)
    if(permission.status.isGranted) {
        //expeditedWork
        val workRequest = OneTimeWorkRequestBuilder<CustomWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }else{
        LaunchedEffect(Unit) {
            permission.launchPermissionRequest()
        }
    }

    Box(modifier = modifier.fillMaxSize()){}
}