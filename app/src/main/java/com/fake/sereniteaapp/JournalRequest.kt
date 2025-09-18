package com.fake.sereniteaapp

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class JournalRequest(
    val remoteId: String? = null,
    val title: String,
    val content: String,
    val dateIso: String,
    val attachmentUri: String?,
    val createdAt: Long
)

data class ApiResponse(val success: Boolean, val id: String?)

//interface ApiService {
//    @POST("/addEntry")
//    suspend fun addEntry(@Body entry: JournalRequest): ApiResponse
//
//    @GET("/entries")
//    suspend fun getEntries(): ApiResponse
//}
