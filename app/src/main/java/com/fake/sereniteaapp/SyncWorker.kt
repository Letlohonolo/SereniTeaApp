package com.fake.sereniteaapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repo = JournalRepository(db.journalDao(), applicationContext, "https://sereniteaapp-2.onrender.com")
        repo.syncUnsynced()
        return Result.success()
    }
}