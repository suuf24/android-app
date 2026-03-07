package com.brikout.crypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.view.WindowCompat
import com.brikout.crypto.domain.ScheduleBreakoutSyncUseCase
import com.brikout.crypto.ui.screen.BreakoutScreen
import com.brikout.crypto.ui.screen.BreakoutViewModel
import com.brikout.crypto.ui.theme.BrikTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BreakoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ScheduleBreakoutSyncUseCase().invoke(this)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            BrikTheme {
                BreakoutScreen(uiState = uiState)
            }
        }
    }
}
