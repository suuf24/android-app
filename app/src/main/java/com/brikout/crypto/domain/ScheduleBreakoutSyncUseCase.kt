package com.brikout.crypto.domain

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.brikout.crypto.worker.BreakoutSyncWorker
import java.util.concurrent.TimeUnit

class ScheduleBreakoutSyncUseCase {
    fun invoke(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val delayMinutes = minutesUntilNextQuarterHour()

        val request = PeriodicWorkRequestBuilder<BreakoutSyncWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(BreakoutSyncWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            BreakoutSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun minutesUntilNextQuarterHour(): Long {
        val nowMs = System.currentTimeMillis()
        val minuteMs = TimeUnit.MINUTES.toMillis(1)
        val quarterMs = TimeUnit.MINUTES.toMillis(15)
        val nextQuarter = ((nowMs / quarterMs) + 1) * quarterMs
        val deltaMinutes = ((nextQuarter - nowMs) / minuteMs)
        return deltaMinutes.coerceAtLeast(1)
    }
}
