package com.fake.sereniteaapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: JournalRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = JournalRepository(db.journalDao(), application, "https://sereniteaapp-2.onrender.com")
    }

    fun entriesForDate(dateIso: String): LiveData<List<JournalEntity>> =
        repo.entriesForDateLive(dateIso)

    fun addEntry(entry: JournalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addLocalEntry(entry)
        }
    }

    fun updateEntry(entry: JournalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateLocalEntry(entry)
        }
    }

    fun syncAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.syncUnsynced()
        }
    }
}