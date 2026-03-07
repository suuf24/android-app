package com.brikout.crypto.data.repository

import com.brikout.crypto.data.local.BreakoutSignalDao
import com.brikout.crypto.data.local.SyncPreferences
import com.brikout.crypto.data.model.BreakoutSignal
import com.brikout.crypto.data.scraper.BybitTickerApi
import com.brikout.crypto.data.scraper.TelegramScraper
import kotlinx.coroutines.flow.Flow

class BreakoutRepository(
    private val scraper: TelegramScraper,
    private val dao: BreakoutSignalDao,
    private val syncPreferences: SyncPreferences,
    private val bybitTickerApi: BybitTickerApi = BybitTickerApi()
) {
    fun observeSignals(): Flow<List<BreakoutSignal>> = dao.observeSignals()

    suspend fun refreshSignals(): Int {
        val scrapedSignals = scraper.scrapeBreakouts()
        if (scrapedSignals.isEmpty()) return 0

        var insertedCount = 0
        var newestTimestamp: String? = null
        scrapedSignals.forEach { signal ->
            if (dao.insert(signal) != -1L) {
                insertedCount++
            }
            if (newestTimestamp == null || signal.timestamp > newestTimestamp!!) {
                newestTimestamp = signal.timestamp
            }
        }

        newestTimestamp?.let { syncPreferences.setLastProcessedTimestamp(it) }
        return insertedCount
    }

    suspend fun fetchLivePrices(symbols: Set<String>): Map<String, Double> {
        if (symbols.isEmpty()) return emptyMap()
        return symbols.associateWith { symbol -> bybitTickerApi.getLastPrice(symbol) }
            .filterValues { it != null }
            .mapValues { it.value!! }
    }

    suspend fun getUnnotifiedSignals(): List<BreakoutSignal> = dao.getUnnotifiedSignals()

    suspend fun markNotified(id: Long) = dao.markNotified(id)

    suspend fun getLastProcessedTimestamp(): String? = syncPreferences.getLastProcessedTimestamp()
}
