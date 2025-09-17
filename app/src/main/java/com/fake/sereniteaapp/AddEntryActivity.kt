package com.fake.sereniteaapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddEntryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var vm: JournalViewModel
    private var pickedUri: Uri? = null
    private var dateIso: String = ""

    private lateinit var btnAttachment: Button
    private lateinit var btnSaveClose: Button
    private lateinit var journalTitle: EditText
    private lateinit var journalContent: EditText
    private lateinit var tvAttachmentName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_entry)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        btnAttachment = findViewById(R.id.btnAttachment)
        btnSaveClose = findViewById(R.id.btnSaveClose)
        journalTitle = findViewById(R.id.journalTitle)
        journalContent = findViewById(R.id.journalContent)
        tvAttachmentName = findViewById(R.id.tvAttachmentName)

        vm = ViewModelProvider(this).get(JournalViewModel::class.java)
        dateIso = intent.getStringExtra("dateIso") ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        btnAttachment.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, 1234)
        }

        btnSaveClose.setOnClickListener {
            val title = journalTitle.text.toString().trim()
            val content = journalContent.text.toString().trim()
            val entry = JournalEntity(
                title = title,
                content = content,
                dateIso = dateIso,
                attachmentUri = pickedUri?.toString(),
                isSynced = false
            )
            vm.addEntry(entry)
            finish()
        }

        setupNavigation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            pickedUri = data?.data
            tvAttachmentName.text = pickedUri?.lastPathSegment ?: "attachment"
            // Persist permission for content URI
            pickedUri?.let { uri ->
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

    //enables the users navigation
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
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
                R.id.journal ->  recreate()
                else -> false
            }
            true
        }
    }
}