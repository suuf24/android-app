package com.brikout.crypto.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.brikout.crypto.data.local.AppDatabase
import com.brikout.crypto.data.local.SyncPreferences
import com.brikout.crypto.data.repository.BreakoutRepository
import com.brikout.crypto.data.scraper.TelegramScraper
import com.brikout.crypto.notification.BreakoutNotificationHelper

class BreakoutSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repository by lazy {
        BreakoutRepository(
            scraper = TelegramScraper(),
            dao = AppDatabase.getInstance(applicationContext).breakoutSignalDao(),
            syncPreferences = SyncPreferences(applicationContext)
        )
    }

    private val notifier by lazy { BreakoutNotificationHelper(applicationContext) }

    override suspend fun doWork(): Result {
        return runCatching {
            repository.refreshSignals()
            val unnotified = repository.getUnnotifiedSignals()
            unnotified.forEach { signal ->
                notifier.notifyNewSignal(signal)
                repository.markNotified(signal.id)
            }
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "breakout_sync_work"
    }
}
