package com.fake.sereniteaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.sereniteaapp.databinding.ItemHabitBinding
import com.fake.sereniteaapp.Habit
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