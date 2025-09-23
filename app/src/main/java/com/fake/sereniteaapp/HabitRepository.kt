package com.fake.sereniteaapp

import com.google.firebase.firestore.FirebaseFirestore

class HabitRepository {

    private val db = FirebaseFirestore.getInstance()
    private val habitRef = db.collection("habits")

    fun addHabit(habit: Habit, onResult: (Boolean) -> Unit) {
        val doc = habitRef.document()
        habit.id = doc.id
        doc.set(habit)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getHabits(onResult: (List<Habit>) -> Unit) {
        habitRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Habit::class.java)
                onResult(list)
            }
    }

    fun updateHabit(habit: Habit, onResult: (Boolean) -> Unit) {
        habit.id?.let {
            habitRef.document(it).set(habit)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }

}