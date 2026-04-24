package com.example.controldeacceso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        
        dbHelper = DatabaseHelper(this)

        val root = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.login_main)
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val etUsuario = findViewById<TextInputEditText>(R.id.etUsuario)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Acción de Login
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()
            
            if (usuario.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Por favor, complete todos los campos.")
            } else {
                val request = LoginRequest(usuario, password)
                RetrofitClient.api.login(request).enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("userName", usuario.substringBefore("@"))
                                putString("userEmail", usuario)
                                apply()
                            }

                            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            showAlert("Error", "Usuario o contraseña incorrectos")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showAlert("Error", "No se pudo conectar con el servidor: ${t.message}")
                    }
                })
            }
        }

        // Ir a la pantalla de Registro
        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAlert(titulo: String, mensaje: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}