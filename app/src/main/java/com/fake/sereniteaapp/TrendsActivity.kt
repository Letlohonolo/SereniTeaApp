package com.fake.sereniteaapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.formatter.ValueFormatter


class TrendsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trends)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        chart = findViewById(R.id.lineChart)

        val uid = auth.currentUser?.uid

        if (uid !=null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        if (document.exists()) {
                            val user = document.toObject(User::class.java)
                            findViewById<TextView>(R.id.Welcome).text =
                                "${user?.name}'s Trends"
                        }
                    }
                }
            loadMoods(uid)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //directs the user to the specific screen
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation()

    }

    //enables the users navigation
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.journal -> startActivity(Intent(this, JournalActivity::class.java))
                R.id.garden -> {
                    Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                }
                R.id.motivation -> startActivity(Intent(this, MotivationActivity::class.java))
                R.id.progress -> startActivity(Intent(this, ProgressActivity::class.java))
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.Trends ->  recreate()
                else -> false
            }
            true
        }
    }

    private fun loadMoods(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("moods")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) return@addOnSuccessListener

                val entries = ArrayList<Entry>()

                result.forEach { doc ->
                    val mood = doc.getString("mood") ?: "Neutral"
                    val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    entries.add(Entry(ts.toFloat(), mood.toFloatValue()))
                }

                // Create LineDataSet
                val dataSet = LineDataSet(entries, "Mood Trends")
                dataSet.color = Color.BLUE
                dataSet.circleRadius = 6f
                dataSet.setDrawValues(false)
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                // Highlight low moods
                entries.forEach { entry ->
                    if (entry.y <= 2f) {
                        dataSet.setCircleColor(Color.MAGENTA)
                    }
                }

                val lineData = LineData(dataSet)
                chart.data = lineData

                // X-axis as date
                chart.xAxis.valueFormatter = object : ValueFormatter() {
                    private val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        return sdf.format(Date(value.toLong()))
                    }
                }
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.xAxis.granularity = 1f
                chart.xAxis.setDrawGridLines(false)

                // Y-axis as moods
                chart.axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            5 -> "Happy"
                            4 -> "Excited"
                            3 -> "Neutral"
                            2 -> "Tired"
                            1 -> "Sad"
                            else -> ""
                        }
                    }
                }
                chart.axisLeft.axisMinimum = 1f
                chart.axisLeft.axisMaximum = 5f
                chart.axisRight.isEnabled = false

                // Additional chart styling
                chart.description.isEnabled = false
                chart.animateX(800)
                chart.legend.isEnabled = true
                chart.invalidate()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load moods: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Map moods to numbers
    private fun String.toFloatValue(): Float {
        return when (this) {
            "Happy" -> 5f
            "Excited" -> 4f
            "Neutral" -> 3f
            "Tired" -> 2f
            "Sad", "Stressed" -> 1f
            else -> 3f
        }
    }
}