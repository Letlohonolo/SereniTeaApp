package com.fake.sereniteaapp

data class Habit(
    var id: String? = null,
    var name: String = "",
    var isCompleted: Boolean = false,
    var lastCompletedDate: String = "",
    var userId: String = ""
)
