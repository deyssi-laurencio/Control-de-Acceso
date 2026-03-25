package com.example.controldeacceso

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class ConfigFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        val btnEditProfile = view.findViewById<LinearLayout>(R.id.btnEditProfile)
        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchNotifications = view.findViewById<SwitchMaterial>(R.id.switchNotifications)
        val tvAppName = view.findViewById<TextView>(R.id.tvAppName)
        val tvAppVersion = view.findViewById<TextView>(R.id.tvAppVersion)

        val sharedPref = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

        // MODO OSCURO
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        switchDarkMode.isChecked = isDarkMode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("darkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // NOTIFICACIONES
        val areNotificationsEnabled = sharedPref.getBoolean("notifications", true)
        switchNotifications.isChecked = areNotificationsEnabled
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notifications", isChecked).apply()
        }

        // EDITAR PERFIL
        btnEditProfile.setOnClickListener {
            val fragment = PerfilFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("modoEdicion", true)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // INFORMACIÓN
        tvAppName.text = "Nombre: Control de Acceso"
        tvAppVersion.text = "Versión: 1.0.0"

        return view
    }
}