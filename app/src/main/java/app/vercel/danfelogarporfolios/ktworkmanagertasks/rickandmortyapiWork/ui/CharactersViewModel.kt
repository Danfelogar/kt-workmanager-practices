package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.Character
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository.NetworkCharactersRepository
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.workers.RickAndMortySyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface CharactersUiState {
    object Loading : CharactersUiState
    data class Success(val values: List<Character>) : CharactersUiState
    object Error : CharactersUiState
}

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val charactersRepository: NetworkCharactersRepository,
    @ApplicationContext private val appContext: Context
): ViewModel(){
    val uiState: StateFlow<CharactersUiState> = charactersRepository.observeCharacters()
        .map { characters ->
            // Transform the stream of character lists into UI state objects
            // Convert raw data into presentation-ready state
            CharactersUiState.Success(characters) as CharactersUiState
        }
        .stateIn(
            // Coroutine scope where the state collection will be launched
            // Tied to ViewModel lifecycle - will be cancelled when ViewModel is cleared
            scope = viewModelScope,

            // Sharing policy configuration:
            // - WhileSubscribed(5000) means the upstream flow will be active when there's at least one subscriber
            // - The 5000ms delay means it waits 5 seconds after the last subscriber unsubscribes before stopping
            // - This prevents restarting the flow on configuration changes (like screen rotation)
            // - Improves performance and prevents data reloading during quick UI state changes
            started = SharingStarted.WhileSubscribed(5_000),

            // Initial value emitted before the first actual value is available
            // Shows loading state immediately while data is being fetched
            // Ensures UI always has a state to display, preventing null cases
            initialValue = CharactersUiState.Loading
        )

    init {
        scheduleCharacterSync()
    }

    private fun scheduleCharacterSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<RickAndMortySyncWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<RickAndMortySyncWorker>()
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(appContext)

        // Periodic (15 min)
        workManager.enqueueUniquePeriodicWork(
            "RickAndMortyPeriodicSync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        // One-time
        workManager.enqueue(oneTimeRequest)
    }


    fun getResources() {
        viewModelScope.launch {
            try {
                charactersRepository.getCharacters()
            } catch (e: IOException) {
                CharactersUiState.Error

            } catch (e: HttpException) {
                CharactersUiState.Error
            }
        }
    }

}