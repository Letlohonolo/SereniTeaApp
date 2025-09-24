package com.fake.sereniteaapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*

class JournalActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var vm: JournalViewModel
    private lateinit var adapter: JournalAdapter
    private var selectedDateIso: String = ""

    private lateinit var tvDate: TextView
    private lateinit var Entries: RecyclerView
    private lateinit var btnAddEntry: Button
    private lateinit var btnChangeDate: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal)

        tvDate = findViewById(R.id.Date)
        Entries = findViewById(R.id.Entries)
        btnAddEntry = findViewById(R.id.btnAddEntry)
        btnChangeDate = findViewById(R.id.btnChangeDate)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        vm = ViewModelProvider(this).get(JournalViewModel::class.java)

        val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDateIso = sdfIso.format(Date())
        val sdfDisplay = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tvDate.text = sdfDisplay.format(Date())

        adapter = JournalAdapter(emptyList()) { entry ->
            //TODO: open entry detail/edit screen
        }

        Entries.layoutManager = LinearLayoutManager(this)
        Entries.adapter = adapter

        observeEntriesForDate(selectedDateIso)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        btnAddEntry.setOnClickListener {
            val i = Intent(this, AddEntryActivity::class.java)
            i.putExtra("dateIso", selectedDateIso)
            startActivity(i)
        }

        btnChangeDate.setOnClickListener {
            showDatePicker()
        }

        setupNavigation()

    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dp = DatePickerDialog(this, { _, y, m, d ->
            val cal2 = Calendar.getInstance()
            cal2.set(y, m, d)
            val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDateIso = sdfIso.format(cal2.time)
            val sdfDisplay = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            tvDate.text = sdfDisplay.format(cal2.time)
            observeEntriesForDate(selectedDateIso)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        dp.show()
    }

    private fun observeEntriesForDate(dateIso: String) {
        vm.entriesForDate(dateIso).observe(this) { list ->
            adapter.submitList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        // sync will happen
        vm.syncAll()

        //refresh the list in case new entries were added
        observeEntriesForDate(selectedDateIso)
    }

    //enables the users navigation
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.garden -> {
                    Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                }
                R.id.motivation -> startActivity(Intent(this, MotivationActivity::class.java))
                R.id.progress -> startActivity(Intent(this, ProgressActivity::class.java))
                R.id.garden -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.Trends -> startActivity(Intent(this, TrendsActivity::class.java))
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.journal ->  recreate()
                else -> false
            }
            true
        }
    }
}