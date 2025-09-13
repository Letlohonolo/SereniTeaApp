package com.fake.sereniteaapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var preferences: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        val user = auth.currentUser
        val uid = auth.currentUser?.uid

        val settingsName = findViewById<EditText>(R.id.settingsName)
        val settingsEmail = findViewById<EditText>(R.id.settingsEmail)
        val settingsPhone = findViewById<EditText>(R.id.settingsPhone)
        val btnSaveInformation = findViewById<Button>(R.id.btnSaveInfo)
        val switchLanguage = findViewById<Switch>(R.id.switchLanguage)
        val switchNotifications = findViewById<Switch>(R.id.switchNotifications)
        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        val switchBiometrics = findViewById<Switch>(R.id.switchBiometrics)
        val btnFeedback = findViewById<Button>(R.id.btnFeedback)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        settingsEmail.setText(user?.email)
        settingsEmail.isEnabled = false

        uid?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        settingsName.setText(document.getString("name")?: "")
                        settingsPhone.setText(document.getString("phone") ?: "")
                    }
                }
        }

        //Loads user preferences
        switchLanguage.isChecked = preferences.getBoolean("LanguageEnglish", true)
        switchNotifications.isChecked = preferences.getBoolean("notifications", true)
        switchTheme.isChecked = preferences.getBoolean("darkMode", false)

        //profile information changes
        btnSaveInformation.setOnClickListener {
            val name = settingsName.text.toString()
            val phone = settingsPhone.text.toString()

            if (user != null) {
                val data = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "email" to user.email
                )

                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "failed to update ", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        //Language change

        //Notifications toggle
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("notifications", isChecked).apply()
            Toast.makeText(this, if (isChecked) "Notifications ON" else "Notifications OFF", Toast.LENGTH_SHORT).show()
        }

        //Theme toggle
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("darkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        //Biometrics Toggle - coming soon(part3)

        //Feedback
        btnFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@sereniteaapp.com")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback for SereniTea App")
            }
            startActivity(intent)
        }

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
                R.id.journal -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.motivation -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.progress -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.garden -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.settings ->  recreate()
                else -> false
            }
            true
        }
    }
}