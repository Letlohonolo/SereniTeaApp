package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class MonthlySummaryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var summaryTextView: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var HomeBtn: Button

    private var currentYear = 0
    private var currentMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_monthly_summary)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        summaryTextView = findViewById(R.id.summaryTextView)
        prevButton = findViewById(R.id.prevMonthButton)
        nextButton = findViewById(R.id.nextMonthButton)
        HomeBtn = findViewById(R.id.HomeBtn)

        val now = Calendar.getInstance()
        currentYear = now.get(Calendar.YEAR)
        currentMonth = now.get(Calendar.MONTH)

        val uid = auth.currentUser?.uid
        if (uid != null) {
            loadMonthlySummary(uid, currentYear, currentMonth)
        }

        prevButton.setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
            uid?.let { loadMonthlySummary(it, currentYear, currentMonth) }
        }

        nextButton.setOnClickListener {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
                currentYear++
            }
            uid?.let { loadMonthlySummary(it, currentYear, currentMonth) }
        }

        HomeBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun loadMonthlySummary(userId: String, year: Int, month: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        db.collection("users")
            .document(userId)
            .collection("moods")
            .whereGreaterThanOrEqualTo("timestamp", startOfMonth)
            .whereLessThan("timestamp", endOfMonth)
            .get()
            .addOnSuccessListener { result ->
                val moodCounts = mutableMapOf<String, Int>()
                val moodValues = mutableListOf<Float>() // for average calculation

                for (doc in result) {
                    val mood = doc.getString("mood") ?: continue
                    moodCounts[mood] = moodCounts.getOrDefault(mood, 0) + 1
                    moodValues.add(mood.toFloatValue())
                }

                showSummary(moodCounts, moodValues, year, month)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading summary", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSummary(moodCounts: Map<String, Int>, moodValues: List<Float>, year: Int, month: Int) {
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault())
            .format(GregorianCalendar(year, month, 1).time)

        if (moodCounts.isEmpty()) {
            summaryTextView.text = "No moods logged in $monthName $year."
            return
        }

        // Calculate average mood score
        val avgScore = moodValues.average().toFloat()
        val avgMoodLabel = avgScore.toMoodLabel()

        val builder = StringBuilder("Mood Summary for $monthName $year:\n\n")
        builder.append("🌟 Average Mood: $avgMoodLabel (${String.format("%.1f", avgScore)})\n\n")

        for ((mood, count) in moodCounts) {
            builder.append("• $mood: $count times\n")
        }

        summaryTextView.text = builder.toString()
    }

    // Map moods to numbers (same as your TrendsActivity)
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

    // Convert numeric average back into a mood label
    private fun Float.toMoodLabel(): String {
        return when (this.toInt()) {
            5 -> "Happy"
            4 -> "Excited"
            3 -> "Neutral"
            2 -> "Tired"
            1 -> "Sad"
            else -> "Neutral"
        }
    }
}