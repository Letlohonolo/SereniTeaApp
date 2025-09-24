package com.fake.sereniteaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.sereniteaapp.databinding.ItemHabitBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Code Attribution
//RecyclerView.Adapter
//Android Developers(2024)
class HabitAdapter (
    //mutable list that will hold all the habits data items
    private var habits: MutableList<Habit>,
    //update to firebase
    private val updateHabitInFirebase: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    //Code Attribution
    //ViewHolder pattern
    //Android Developers(2024)
    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    //inflates the layout
    //returns a new view holder
    //Code Attribution
    //LayoutInflater
    //Android Developers(2024)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    //binds data to each item in the recyclerView
    //Code Attribution
    //RecyclerView.Adapter
    //Android Developers(2024)
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.binding.apply {
            habitName.text = habit.name

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            habitCheckBox.setOnCheckedChangeListener(null)
            habitCheckBox.isChecked = habit.lastCompletedDate == today

            habitCheckBox.setOnCheckedChangeListener(null)

            //when the box get checked this will happen
            habitCheckBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    //the habit will save these aspects in the database
                    if (habit.lastCompletedDate != today) {
                        habit.isCompleted = true
                        habit.lastCompletedDate = today
                        updateHabitInFirebase(habit)
                    }
                } else {
                    //  allow unchecking
                    habit.isCompleted = false
                    habit.lastCompletedDate = ""
                    updateHabitInFirebase(habit)
                }
            }

            }
        }


    override fun getItemCount(): Int = habits.size

    fun setData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}