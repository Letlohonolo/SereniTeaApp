package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MoodLogActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mood_log)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid ?: return

        findViewById<Button>(R.id.btnHappy).setOnClickListener {
            saveMood(userId, "Happy")
        }
        findViewById<Button>(R.id.btnSad).setOnClickListener {
            saveMood(userId, "Sad")
        }
        findViewById<Button>(R.id.btnStressed).setOnClickListener {
            saveMood(userId, "Stressed")
        }
        findViewById<Button>(R.id.btnExcited).setOnClickListener {
            saveMood(userId, "Excited")
        }
        findViewById<Button>(R.id.btnTired).setOnClickListener {
            saveMood(userId, "Tired")
        }
        findViewById<Button>(R.id.btnHome).setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    private fun saveMood(userId: String, mood: String) {
        val entry = hashMapOf(
            "mood" to mood,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("moods")
            .add(entry)
            .addOnSuccessListener {
                Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving mood", Toast.LENGTH_SHORT).show()
            }
    }
}