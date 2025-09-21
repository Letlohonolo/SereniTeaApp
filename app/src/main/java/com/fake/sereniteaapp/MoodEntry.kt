package com.fake.sereniteaapp

data class MoodEntry(
    val mood: String = "",
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
