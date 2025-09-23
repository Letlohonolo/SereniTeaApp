package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.XAxis
import java.text.SimpleDateFormat
import java.util.*

class ProgressActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progress)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation()

        val lineChart: LineChart = findViewById(R.id.habitLineChart)

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("habits")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    val habits = result.toObjects(Habit::class.java)
                    showWeeklyTrend(lineChart, habits)
                }
        }
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
                R.id.journal -> startActivity(Intent(this, JournalActivity::class.java))
                R.id.Trends -> startActivity(Intent(this, TrendsActivity::class.java))
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.progress ->  recreate()
                else -> false
            }
            true
        }
    }

//

    private fun showWeeklyTrend(lineChart: LineChart, habits: List<Habit>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val last7Days = mutableMapOf<String, Int>()
        val last7DaysLabels = mutableListOf<String>()

        // Fill last 7 days with default 0
        for (i in 6 downTo 0) {
            val day = Calendar.getInstance()
            day.add(Calendar.DAY_OF_YEAR, -i)
//            last7Days[dateFormat.format(day.time)] = 0
            val dateStr = dateFormat.format(day.time)
            last7Days[dateStr] = 0
            last7DaysLabels.add(dayFormat.format(day.time))
        }

        // Count completed habits per day
        for (habit in habits) {
            if (habit.lastCompletedDate.isNotEmpty() && last7Days.containsKey(habit.lastCompletedDate)) {
                last7Days[habit.lastCompletedDate] = last7Days[habit.lastCompletedDate]!! + 1
            }
        }

        // Convert map to chart entries
        val entries = ArrayList<Entry>()
        var index = 0f
        for (date in last7Days.keys) {
            entries.add(Entry(index, last7Days[date]!!.toFloat()))
            index++
        }


        val dataSet = LineDataSet(entries, "Habits Completed")
        dataSet.color = resources.getColor(R.color.black, theme)
        dataSet.valueTextColor = resources.getColor(R.color.black, theme)
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 6f
        dataSet.setCircleColor(resources.getColor(R.color.black, theme))
        dataSet.setDrawFilled(true)
        dataSet.fillColor = resources.getColor(R.color.PrimaryButton, theme)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        lineData.setValueTextSize(12f)
        lineChart.data = lineData

        // X-axis labels
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(last7DaysLabels)
        xAxis.textSize = 12f


        // Y-axis formatting
        lineChart.axisRight.isEnabled = false
        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.granularity = 1f
        yAxis.textSize = 12f

        // Chart styling
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setBackgroundColor(resources.getColor(R.color.Background, theme))
        lineChart.animateX(1000) // animate X axis
        lineChart.invalidate()
    }

}