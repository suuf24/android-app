package com.brikout.crypto.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breakout_signals",
    indices = [Index(value = ["symbol", "timestamp"], unique = true)]
)
data class BreakoutSignal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val timeframe: String,
    val price: Double,
    val timestamp: String,
    val notified: Boolean = false
)
