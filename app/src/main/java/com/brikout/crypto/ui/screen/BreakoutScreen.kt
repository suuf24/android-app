package com.brikout.crypto.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brikout.crypto.data.model.BreakoutSignal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakoutScreen(uiState: BreakoutUiState) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "BRIK Breakout Signals") })
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.signals.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.signals, key = { it.id }) { signal ->
                        BreakoutSignalCard(signal)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakoutSignalCard(signal: BreakoutSignal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = signal.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Timeframe: ${signal.timeframe}")
            Text(text = "Price: ${signal.price}")
            Text(text = "Timestamp: ${signal.timestamp.toPrettyTimestamp()}")
        }
    }
}

private fun String.toPrettyTimestamp(): String {
    return runCatching {
        val parsed = OffsetDateTime.parse(this)
        parsed.format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
        )
    }.getOrDefault(this)
}
