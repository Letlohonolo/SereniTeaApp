package com.fake.sereniteaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import android.widget.Button
import androidx.activity.viewModels
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
    private val viewModel: QuoteViewModel by viewModels()
    private lateinit var quoteTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_motivation)
        val tvQuote = findViewById<TextView>(R.id.tvQuote)
        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)

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

//        // Observe LiveData from ViewModel
//        viewModel.quote.observe(this, Observer { quote ->
//            tvQuote.text = "\"${quote.text}\""
//            tvAuthor.text = "- ${quote.author}"
//        })
//
//        // Load the daily quote
//        viewModel.loadDailyQuote()

        quoteTextView = findViewById(R.id.tvQuote)
        fetchQuotesFromFirestore()
    }

    private fun fetchQuotesFromFirestore() {
        db.collection("quotes")
            .get()
            .addOnSuccessListener { result ->
                val firestoreQuotes = result.documents.mapNotNull { it.getString("text") }

                Log.d("MotivationActivity", "Fetched ${firestoreQuotes.size} quotes from Firestore")

                if (firestoreQuotes.isNotEmpty()) {
                    val randomQuote = firestoreQuotes.random()
                    quoteTextView.text = randomQuote
                    Log.d("MotivationActivity", "Random Firestore quote = $randomQuote")
                } else {
                    quoteTextView.text = "No quotes found."
                    Log.d("MotivationActivity", "Firestore collection is empty")
                }
            }
            .addOnFailureListener { e ->
                quoteTextView.text = "Failed to load quotes."
                Log.e("MotivationActivity", "Error fetching quotes", e)
            }
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