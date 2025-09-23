package com.fake.sereniteaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.sereniteaapp.databinding.ItemHabitBinding
import com.fake.sereniteaapp.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitAdapter (
    private var habits: MutableList<Habit>,
    private val onChecked: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.binding.apply {
            habitName.text = habit.name
            habitCheckBox.isChecked = habit.isCompleted

            habitCheckBox.setOnCheckedChangeListener { _, checked ->
                habit.isCompleted = checked

                // ✅ update lastCompletedDate whenever the user toggles a habit
                habit.lastCompletedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                onChecked(habit)
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