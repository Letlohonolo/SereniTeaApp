package com.fake.sereniteaapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val habitRef = db.collection("habits")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    fun addHabit(habit: Habit, callback: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return callback(false)
        habit.userId = uid

        val docRef = habitRef.document()
        habit.id = docRef.id
        docRef.set(habit)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getHabits(callback: (List<Habit>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return callback(emptyList())

        habitRef.whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                val habits = result.toObjects(Habit::class.java)
                callback(habits)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    fun updateHabit(habit: Habit, onResult: (Boolean) -> Unit) {
        if (habit.id.isNullOrEmpty()) {
            onResult(false)
            return
        }
        habitRef.document(habit.id!!).set(habit)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }



}