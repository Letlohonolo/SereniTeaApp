package com.fake.sereniteaapp

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class JournalRepository (
    private val dao: JournalDao,
    private val context: Context,
    private val renderApiUrl: String
) {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun entriesForDateLive(dateIso: String) = dao.entriesForDate(dateIso)

    fun allEntries() = dao.getAllEntries()

    suspend fun addLocalEntry(entry: JournalEntity) {
        dao.insert(entry)
        if (isOnline()) {
            syncEntry(entry)
        }
    }

    suspend fun updateLocalEntry(entry: JournalEntity) {
        dao.update(entry)
        if (isOnline() && !entry.isSynced) {
            syncEntry(entry)
        }
    }

    suspend fun syncUnsynced() {
        val unsynced = dao.unsyncedEntries()
        for (entry in unsynced) {
            syncEntry(entry)
        }
    }

    private suspend fun syncEntry(entry: JournalEntity) = withContext(Dispatchers.IO) {
        val payload = mapOf(
            "localId" to entry.localId,
            "title" to entry.title,
            "content" to entry.content,
            "dateIso" to entry.dateIso,
            "attachmentUri" to entry.attachmentUri,
            "createdAt" to entry.createdAt
        )

        val json = gson.toJson(payload)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$renderApiUrl/addEntry") // Ensure correct endpoint
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) {
                    // ✅ mark as synced
                    val updated = entry.copy(isSynced = true)
                    dao.update(updated)
                } else {
                    // server returned error — keep unsynced
                }
            }
        } catch (e: IOException) {
            // network error — keep unsynced
        }
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Matches your Render API response
    data class ApiResponse(
        val success: Boolean,
        val id: String? = null
    )

}