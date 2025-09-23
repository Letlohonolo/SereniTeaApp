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

    fun addHabit(habit: Habit, onResult: (Boolean) -> Unit) {

        val doc = habitRef.document()
        habit.id = doc.id
        habit.lastCompletedDate = today
        doc.set(habit)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getHabits(onResult: (List<Habit>) -> Unit) {
        habitRef.get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<Habit>()

                for (doc in snapshot.documents) {
                    val habit = doc.toObject(Habit::class.java)
                    if (habit != null) {
                        habit.id = doc.id

                        // Reset if lastCompletedDate is not today
                        if (habit.lastCompletedDate != today) {
                            habit.isCompleted = false
                            habit.lastCompletedDate = today
                            updateHabit(habit) {} // update Firestore
                        }

                        list.add(habit)
                    }
                }

                onResult(list)
            }
    }

//    fun getHabits(onResult: (List<Habit>) -> Unit) {
//        habitRef.get()
//            .addOnSuccessListener { snapshot ->
//                val list = snapshot.toObjects(Habit::class.java)
//                onResult(list)
//            }
//    }

    fun updateHabit(habit: Habit, onResult: (Boolean) -> Unit) {
        habit.id?.let {
            habitRef.document(it).set(habit)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }

}