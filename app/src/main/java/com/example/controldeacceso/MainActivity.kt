package com.example.controldeacceso

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar Modo Oscuro antes de super.onCreate
        applyDarkMode()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configurar Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Cargar datos de usuario en el header del Navigation Drawer
        updateNavHeader()

        // Configurar icono de búsqueda
        findViewById<ImageView>(R.id.ivSearchIcon).setOnClickListener {
            replaceFragment(BusquedaFragment())
        }

        // Cargar InicioFragment por defecto
        if (savedInstanceState == null) {
            replaceFragment(InicioFragment())
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> replaceFragment(InicioFragment())
                R.id.nav_historial -> replaceFragment(HistorialFragment())
                R.id.nav_personal -> replaceFragment(PersonalFragment())
                R.id.nav_config -> replaceFragment(ConfigFragment())
            }
            true
        }

        // Drawer Navigation (Implementación de Mi Perfil)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(InicioFragment())
                    bottomNav.selectedItemId = R.id.nav_inicio
                }
                R.id.nav_profile -> {
                    replaceFragment(PerfilFragment())
                }
                R.id.nav_logout -> finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun applyDarkMode() {
        val sharedPref = getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun updateNavHeader() {
        val headerView = navView.getHeaderView(0)
        val tvNavName = headerView.findViewById<TextView>(R.id.tvNavName)
        val tvNavEmail = headerView.findViewById<TextView>(R.id.tvNavEmail)
        val ivNavProfile = headerView.findViewById<ImageView>(R.id.ivUserProfileInHeader)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("userName", "Admin Colegio")
        val email = sharedPref.getString("userEmail", "admin@mercedes.edu.pe")
        val photoUriString = sharedPref.getString("userPhotoUri", null)

        tvNavName.text = name
        tvNavEmail.text = email
        if (photoUriString != null) {
            ivNavProfile.setImageURI(Uri.parse(photoUriString))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}