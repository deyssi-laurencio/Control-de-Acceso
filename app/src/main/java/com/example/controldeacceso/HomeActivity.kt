package com.example.controldeacceso

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var ivUserProfile: ImageView

    private val getGalleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            ivUserProfile.setImageURI(data?.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Configurar Toolbar y Drawer
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_historial -> {
                    Toast.makeText(this, "Abriendo Historial...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_personal -> {
                    Toast.makeText(this, "Abriendo Personal...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_config -> {
                    Toast.makeText(this, "Configuraciones", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Lógica de botones principales
        findViewById<MaterialCardView>(R.id.btnRegistrarAcceso).setOnClickListener {
            Toast.makeText(this, "Seleccionando método de acceso (DNI/Rostro)...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.btnHistorial).setOnClickListener {
            Toast.makeText(this, "Cargando historial de accesos...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.btnPersonal).setOnClickListener {
            Toast.makeText(this, "Gestión de Personal", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.btnIoT).setOnClickListener {
            Toast.makeText(this, "Dispositivos IoT (ESP32/Arduino)", Toast.LENGTH_SHORT).show()
        }

        // Configurar el header del Drawer para la foto (opcional si quieres que se vea ahí también)
        val headerView = navView.getHeaderView(0)
        ivUserProfile = headerView.findViewById(R.id.ivUserProfileInHeader) ?: ImageView(this) // Fallback simple
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}