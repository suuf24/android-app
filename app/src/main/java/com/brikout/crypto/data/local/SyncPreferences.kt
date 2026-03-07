package com.brikout.crypto.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")

class SyncPreferences(private val context: Context) {
    private val lastProcessedTimestampKey: Preferences.Key<String> =
        stringPreferencesKey("last_processed_timestamp")

    suspend fun getLastProcessedTimestamp(): String? {
        return context.dataStore.data.map { it[lastProcessedTimestampKey] }.first()
    }

    suspend fun setLastProcessedTimestamp(value: String) {
        context.dataStore.edit { prefs ->
            prefs[lastProcessedTimestampKey] = value
        }
    }
}
