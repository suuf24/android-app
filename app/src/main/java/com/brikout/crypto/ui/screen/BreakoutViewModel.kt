package com.brikout.crypto.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brikout.crypto.data.local.AppDatabase
import com.brikout.crypto.data.local.SyncPreferences
import com.brikout.crypto.data.model.BreakoutSignal
import com.brikout.crypto.data.repository.BreakoutRepository
import com.brikout.crypto.data.scraper.TelegramScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BreakoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BreakoutRepository(
        scraper = TelegramScraper(),
        dao = AppDatabase.getInstance(application).breakoutSignalDao(),
        syncPreferences = SyncPreferences(application)
    )

    private val _uiState = MutableStateFlow(BreakoutUiState())
    val uiState: StateFlow<BreakoutUiState> = _uiState.asStateFlow()

    init {
        observeSignals()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { repository.refreshSignals() }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun observeSignals() {
        viewModelScope.launch {
            repository.observeSignals().collectLatest { signals ->
                _uiState.value = _uiState.value.copy(signals = signals, error = null)
            }
        }
    }
}

data class BreakoutUiState(
    val isLoading: Boolean = false,
    val signals: List<BreakoutSignal> = emptyList(),
    val error: String? = null
)
