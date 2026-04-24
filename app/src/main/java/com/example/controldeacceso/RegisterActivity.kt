package com.example.controldeacceso

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

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
        val btnVolverLogin = findViewById<ImageView>(R.id.btnVolverLogin)

        btnRegistrarConfirm.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Por favor, complete todos los campos.")
            } else if (password != confirmPassword) {
                showAlert("Error", "Las contraseñas no coinciden.")
            } else {
                val request = LoginRequest(usuario, password)
                RetrofitClient.api.registrar(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            MaterialAlertDialogBuilder(this@RegisterActivity)
                                .setTitle("Registro Exitoso")
                                .setMessage("El usuario $usuario ha sido guardado.")
                                .setPositiveButton("Aceptar") { _, _ ->
                                    finish()
                                }
                                .show()
                        } else {
                            showAlert("Error", "Error al registrar el usuario.")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showAlert("Error", "No se pudo conectar con el servidor: ${t.message}")
                    }
                })
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