package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MotivationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: QuoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_motivation)
        val tvQuote = findViewById<TextView>(R.id.tvQuote)
        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)
        val btnShare = findViewById<Button>(R.id.btnShare)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //directs the user to the specific screen
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation()

        // Observe LiveData from ViewModel
        viewModel.quote.observe(this, Observer { quote ->
            tvQuote.text = "\"${quote.text}\""
            tvAuthor.text = "- ${quote.author}"

            btnShare.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "\"${quote.text}\" - ${quote.author}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        })

        // Load the daily quote
        viewModel.loadDailyQuote()
    }

    //enables the users navigation
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.journal -> startActivity(Intent(this, JournalActivity::class.java))
                R.id.challenges -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.progress -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.garden -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.Trends -> startActivity(Intent(this, TrendsActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.signOut -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.motivation ->  recreate()
                else -> false
            }
            true
        }
    }
}