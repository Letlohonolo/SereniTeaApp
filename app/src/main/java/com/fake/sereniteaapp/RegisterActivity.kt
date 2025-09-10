package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val name = findViewById<EditText>(R.id.RegisterName).text.toString()
            val email = findViewById<EditText>(R.id.RegisterEmail).text.toString()
            val password = findViewById<EditText>(R.id.RegisterPassword).text.toString()
            registerUser(email, password, name)
        }

        findViewById<TextView>(R.id.RegisterLink).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        saveUserToFirestore(user.uid, name, email)
                    }
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(uid: String, name: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = User(uid = uid, name = name, email = email)
        db.collection("users").document()
            .set(user)
            .addOnSuccessListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }

    }
}