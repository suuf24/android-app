package com.brikout.crypto.data.scraper

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup

class BybitTickerApi {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getLastPrice(symbol: String): Double? {
        val responseBody = runCatching {
            Jsoup.connect("$BASE_URL?category=linear&symbol=${symbol.uppercase()}")
                .ignoreContentType(true)
                .userAgent(USER_AGENT)
                .timeout(10_000)
                .execute()
                .body()
        }.getOrNull() ?: return null

        return runCatching {
            val root = json.parseToJsonElement(responseBody).jsonObject
            val list = root["result"]?.jsonObject?.get("list")?.jsonArray
            val first = list?.firstOrNull()?.jsonObject
            first?.get("lastPrice")?.jsonPrimitive?.content?.toDoubleOrNull()
        }.getOrNull()
    }

    companion object {
        private const val BASE_URL = "https://api.bybit.com/v5/market/tickers"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
    }
}
