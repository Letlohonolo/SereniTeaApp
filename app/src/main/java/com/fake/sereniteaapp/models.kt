package com.fake.sereniteaapp

data class Task(
    val id: String = "",
    val title: String = "",
    val details: String? = null,
    val order: Int = 0
)

data class Challenge(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val color: String? = null,
    val durationDays: Int = 0,
    val tasks: List<Task> = emptyList()
)

//data class ApiResponse<T>(val ok: Boolean, val data: T?, val error: String?)
