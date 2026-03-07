package com.brikout.crypto.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brikout.crypto.data.model.BreakoutSignal
import kotlinx.coroutines.flow.Flow

@Dao
interface BreakoutSignalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(signal: BreakoutSignal): Long

    @Query("SELECT * FROM breakout_signals ORDER BY timestamp DESC")
    fun observeSignals(): Flow<List<BreakoutSignal>>

    @Query("SELECT * FROM breakout_signals WHERE notified = 0 ORDER BY timestamp ASC")
    suspend fun getUnnotifiedSignals(): List<BreakoutSignal>

    @Query("UPDATE breakout_signals SET notified = 1 WHERE id = :id")
    suspend fun markNotified(id: Long)
}
