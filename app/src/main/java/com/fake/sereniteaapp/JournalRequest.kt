package com.fake.sereniteaapp

data class JournalRequest(
    val remoteId: String? = null,
    val title: String,
    val content: String,
    val dateIso: String,
    val attachmentUri: String?,
    val createdAt: Long
)

data class ApiResponse(
    val success: Boolean,
    val id: String?
)
