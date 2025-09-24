package com.fake.sereniteaapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Defines the endpoints that your Render API exposes.
 * Retrofit will auto-generate the implementation at runtime.
 */
interface ApiService {

    // Add a new journal entry
    @POST("addEntry")
    suspend fun addEntry(@Body entry: JournalRequest): Response<ApiResponse>

    // Get all journal entries (if your backend supports it)
    @GET("entries")
    suspend fun getAllEntries(): Response<List<JournalEntity>>

    // Get entries for a specific date
    @GET("entries/{dateIso}")
    suspend fun getEntriesForDate(@Path("dateIso") dateIso: String): Response<List<JournalEntity>>

    @POST("addEntryRequest")
    suspend fun addEntryRequest(@Body entry: JournalRequest): ApiResponse

    @GET("entriesRequest")
    suspend fun getEntriesRequest(): ApiResponse

    @GET("daily-quote")
    suspend fun getDailyQuote(): Response<Quote>


}
