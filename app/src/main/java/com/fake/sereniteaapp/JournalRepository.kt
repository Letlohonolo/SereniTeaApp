package com.fake.sereniteaapp

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class JournalRepository (
    private val dao: JournalDao,
    private val context: Context,
//    private val renderApiUrl: String
) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    suspend fun addEntryToFirebase(entry: JournalEntity) {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val data = mapOf(
            "title" to entry.title,
            "content" to entry.content,
            "dateIso" to entry.dateIso,
            "attachmentUri" to entry.attachmentUri,
            "createdAt" to entry.createdAt
        )

        try {
            db.collection("users")
                .document(userId)
                .collection("journal")
                .add(data)
                .await() // requires kotlinx-coroutines-play-services
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private val api = RetrofitClient.api
    private val gson = Gson()
//    fun entriesForDateLive(dateIso: String) = dao.entriesForDate(dateIso)
//    fun allEntries() = dao.getAllEntries()

    fun entriesForDateLive(dateIso: String): LiveData<List<JournalEntity>> =
        dao.entriesForDate(dateIso)
    fun allEntries(): LiveData<List<JournalEntity>> =
        dao.getAllEntries()

//    private val client = OkHttpClient()
//    private val gson = Gson()
//
//    fun entriesForDateLive(dateIso: String) = dao.entriesForDate(dateIso)
//
//    fun allEntries() = dao.getAllEntries()

    suspend fun addLocalEntry(entry: JournalEntity) {
        dao.insert(entry)
        addEntryToFirebase(entry)
        if (isOnline()) {
            syncEntry(entry)
        }
    }

    suspend fun updateLocalEntry(entry: JournalEntity) {
        dao.update(entry)
        addEntryToFirebase(entry)
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
        try {
            val request = JournalRequest(
                remoteId = entry.remoteId,
                title = entry.title,
                content = entry.content,
                dateIso = entry.dateIso,
                attachmentUri = entry.attachmentUri,
                createdAt = entry.createdAt
            )

            val response = api.addEntryRequest(request)

            if (response.success) {
                // ✅ mark as synced and save remoteId if returned
                val updated = entry.copy(
                    isSynced = true,
                    remoteId = response.id ?: entry.remoteId
                )
                dao.update(updated)
            }
        } catch (e: Exception) {
            // ❌ network/server error – keep unsynced
        }
    }



//    private suspend fun syncEntry(entry: JournalEntity) = withContext(Dispatchers.IO) {
//        val payload = mapOf(
//            "localId" to entry.localId,
//            "title" to entry.title,
//            "content" to entry.content,
//            "dateIso" to entry.dateIso,
//            "attachmentUri" to entry.attachmentUri,
//            "createdAt" to entry.createdAt
//        )
//
//        val json = gson.toJson(payload)
//        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
//
//        val request = Request.Builder()
//            .url("$renderApiUrl/addEntry") // Ensure correct endpoint
//            .post(body)
//            .build()
//
//        try {
//            client.newCall(request).execute().use { resp ->
//                if (resp.isSuccessful) {
//                    // ✅ mark as synced
//                    val updated = entry.copy(isSynced = true)
//                    dao.update(updated)
//                } else {
//                    // server returned error — keep unsynced
//                }
//            }
//        } catch (e: IOException) {
//            // network error — keep unsynced
//        }
//    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

//    private fun isOnline(): Boolean {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
//        val network = cm.activeNetwork ?: return false
//        val caps = cm.getNetworkCapabilities(network) ?: return false
//        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
//    }

    // Matches your Render API response
//    data class ApiResponse(
//        val success: Boolean,
//        val id: String? = null
//    )

}