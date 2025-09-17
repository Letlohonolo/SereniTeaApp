package com.fake.sereniteaapp

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntity) : Long

    @Update
    suspend fun update(entry: JournalEntity)

    @DELETE
    suspend fun delete(entry: JournalEntity)

    @Query("SELECT * FROM journal_entries WHERE dateIso =:date ORDER BY createdAt DESC")
    fun entriesForDate(date: String): LiveData<List<JournalEntity>>

    @Query("SELECT * FROM journal_entries WHERE isSynced = 0")
    suspend fun unsyncedEntries(): List<JournalEntity>
}