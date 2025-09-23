package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog
import com.fake.sereniteaapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HabitAdapter
    private val repo = HabitRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid
        if (uid !=null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        if (document.exists()) {
                            val user = document.toObject(User::class.java)
                            findViewById<TextView>(R.id.Welcome).text =
                                "Welcome, ${user?.name} to SereniTea"
                        }
                    }
                }
        }

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation()

        adapter = HabitAdapter(mutableListOf()) { habit ->

            repo.updateHabit(habit) { success ->
                if (!success) {

                    Toast.makeText(this, "Failed to update habit", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.habitRecycler.layoutManager = LinearLayoutManager(this)
        binding.habitRecycler.adapter = adapter

        binding.btnAddHabit.setOnClickListener { showAddHabitDialog() }

        findViewById<Button>(R.id.btnMood).setOnClickListener{
            startActivity(Intent(this, MoodLogActivity::class.java))
        }

        loadHabits()
    }

    //enables the users navigation
    private fun setupNavigation() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.journal -> startActivity(Intent(this, JournalActivity::class.java))
                R.id.challenges -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.motivation -> startActivity(Intent(this, MotivationActivity::class.java))
                R.id.progress -> startActivity(Intent(this, ProgressActivity::class.java))
                R.id.garden -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.Trends -> startActivity(Intent(this, TrendsActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.home -> recreate()
                else -> false
            }
            true
        }
    }

    private fun loadHabits() {
        repo.getHabits { habits ->
            adapter.setData(habits)
        }
    }

    private fun showAddHabitDialog() {
        val input = TextInputEditText(this)
        AlertDialog.Builder(this)
            .setTitle("New Habit")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    val habit = Habit(name = name)
                    repo.addHabit(habit) { success ->
                        if (success) loadHabits()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}