package com.example.controldeacceso

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        val root = findViewById<View>(R.id.register_main)
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val etUsuario = findViewById<TextInputEditText>(R.id.etUsuarioRegister)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordRegister)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPasswordRegister)
        val btnRegistrarConfirm = findViewById<Button>(R.id.btnRegistrarConfirm)
        val btnVolverLogin = findViewById<ImageView>(R.id.btnVolverLogin) // Cambiado a ImageView

        btnRegistrarConfirm.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Por favor, complete todos los campos.")
            } else if (password != confirmPassword) {
                showAlert("Error", "Las contraseñas no coinciden.")
            } else {
                val exito = dbHelper.registrarUsuario(usuario, password)
                if (exito) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("El usuario $usuario ha sido guardado.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            finish() // Volver al Login
                        }
                        .show()
                } else {
                    showAlert("Error", "El usuario ya existe o hubo un problema.")
                }
            }
        }

        btnVolverLogin?.setOnClickListener {
            finish()
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