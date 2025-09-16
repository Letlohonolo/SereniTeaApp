package com.fake.sereniteaapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val remoteId: String? = null,
    val title: String = "",
    val content: String = "",
    val dateIso: String = "",
    val attachmentUri: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
