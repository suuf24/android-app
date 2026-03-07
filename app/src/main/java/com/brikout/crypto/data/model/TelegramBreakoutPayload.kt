package com.brikout.crypto.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelegramBreakoutPayload(
    val event: String? = null,
    val symbol: String? = null,
    val timeframe: String? = null,
    val price: Double? = null,
    val timestamp: String? = null,
    @SerialName("message") val ignoredMessage: String? = null
)
