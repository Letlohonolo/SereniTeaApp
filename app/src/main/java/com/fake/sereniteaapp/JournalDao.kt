package com.fake.sereniteaapp

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries WHERE dateIso = :dateIso ORDER BY createdAt DESC")
    fun entriesForDate(dateIso: String): LiveData<List<JournalEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY dateIso DESC")
    fun getAllEntries(): LiveData<List<JournalEntity>>

    @Query("SELECT * FROM journal_entries WHERE isSynced = 0")
    suspend fun unsyncedEntries(): List<JournalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntity)

    @Update
    suspend fun update(entry: JournalEntity)

}