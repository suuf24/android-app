package com.brikout.crypto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brikout.crypto.data.model.BreakoutSignal
import java.net.URLEncoder
import java.util.Locale

@Composable
fun BreakoutScreen(uiState: BreakoutUiState) {
    Scaffold { paddingValues ->
        when {
            uiState.isLoading && uiState.signals.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.signals,
                        key = { "${it.symbol}-${it.timestamp}" },
                        contentType = { "breakout_signal_card" }
                    ) { signal ->
                        BreakoutSignalCard(
                            signal = signal,
                            currentPrice = uiState.livePrices[signal.symbol.uppercase()]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakoutSignalCard(signal: BreakoutSignal, currentPrice: Double?) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = signal.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(999.dp)),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = signal.timeframe,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PricePanel(
                    modifier = Modifier.weight(1f),
                    label = "BREAKOUT",
                    value = signal.price.toDisplayPrice()
                )
                PricePanel(
                    modifier = Modifier.weight(1f),
                    label = "LIVE",
                    value = currentPrice?.toDisplayPrice() ?: "--"
                )
            }

            Button(
                onClick = {
                    uriHandler.openUri(buildTradingViewLink(signal.symbol, signal.timeframe))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Open TradingView")
            }
        }
    }
}

@Composable
private fun PricePanel(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun buildTradingViewLink(symbol: String, timeframe: String): String {
    val encodedSymbol = URLEncoder.encode("BYBIT:${symbol.uppercase()}", "UTF-8")
    val interval = when (timeframe.lowercase()) {
        "15m" -> "15"
        "1h" -> "60"
        else -> "15"
    }
    return "https://www.tradingview.com/chart/?symbol=$encodedSymbol&interval=$interval"
}

private fun Double.toDisplayPrice(): String {
    return String.format(Locale.US, "%.6f", this).trimEnd('0').trimEnd('.').ifEmpty { "0" }
}
