package app.vercel.danfelogarporfolios.ktworkmanagertasks

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.vercel.danfelogarporfolios.ktworkmanagertasks.ui.theme.KTWorkManagerTasksTheme
import app.vercel.danfelogarporfolios.ktworkmanagertasks.workManagerDemo.CustomWorker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workRequest = OneTimeWorkRequestBuilder<CustomWorker>()
            .setInitialDelay(Duration.ofSeconds(10))
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        enableEdgeToEdge()
        setContent {
            KTWorkManagerTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}