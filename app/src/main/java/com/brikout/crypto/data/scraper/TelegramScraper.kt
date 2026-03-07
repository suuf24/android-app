package com.brikout.crypto.data.scraper

import com.brikout.crypto.data.model.BreakoutSignal
import com.brikout.crypto.data.model.TelegramBreakoutPayload
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class TelegramScraper {
    private val parser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun scrapeBreakouts(channelUrl: String = DEFAULT_CHANNEL_URL): List<BreakoutSignal> {
        return runCatching {
            val doc = Jsoup.connect(channelUrl)
                .userAgent(USER_AGENT)
                .timeout(15_000)
                .get()

            doc.select("div.tgme_widget_message_text")
                .flatMap { element ->
                    val blocksFromHtml = extractJsonBlocks(element.html())
                    if (blocksFromHtml.isNotEmpty()) {
                        blocksFromHtml
                    } else {
                        extractJsonBlocks(element.text())
                    }
                }
                .mapNotNull(::parseBreakout)
                .distinctBy { "${it.symbol}_${it.timestamp}" }
        }.getOrElse { emptyList() }
    }

    private fun extractJsonBlocks(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        return JSON_BLOCK_REGEX.findAll(content)
            .map { htmlEntityCleanup(it.value) }
            .toList()
    }

    private fun htmlEntityCleanup(value: String): String {
        return value
            .replace("&quot;", "\"")
            .replace("&#34;", "\"")
            .replace("&amp;", "&")
            .trim()
    }

    private fun parseBreakout(rawJson: String): BreakoutSignal? {
        val payload = runCatching {
            parser.decodeFromString<TelegramBreakoutPayload>(rawJson)
        }.getOrNull() ?: return null

        if (payload.event != "breakout") return null
        val symbol = payload.symbol ?: return null
        val timeframe = payload.timeframe ?: return null
        val price = payload.price ?: return null
        val timestamp = payload.timestamp ?: return null

        return BreakoutSignal(
            symbol = symbol,
            timeframe = timeframe,
            price = price,
            timestamp = timestamp
        )
    }

    companion object {
        private const val DEFAULT_CHANNEL_URL = "https://t.me/s/BybitSn"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
        private val JSON_BLOCK_REGEX = Regex("\\{[\\s\\S]*?\\}")
    }
}
