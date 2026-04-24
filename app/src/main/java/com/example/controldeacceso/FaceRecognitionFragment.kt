package com.example.controldeacceso

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FaceRecognitionFragment : Fragment() {

    private lateinit var tvStatus: TextView
    private lateinit var btnStartScan: Button
    private lateinit var pbScanning: ProgressBar
    private lateinit var viewScanLine: View
    private lateinit var ivCameraPreview: ImageView

    private var listaPersonas: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_face_recognition, container, false)

        tvStatus = view.findViewById(R.id.tvStatus)
        btnStartScan = view.findViewById(R.id.btnStartScan)
        pbScanning = view.findViewById(R.id.pbScanning)
        viewScanLine = view.findViewById(R.id.viewScanLine)
        ivCameraPreview = view.findViewById(R.id.ivCameraPreview)

        cargarPersonal()

        btnStartScan.setOnClickListener {
            iniciarEscaneo()
        }

        return view
    }

    private fun cargarPersonal() {
        RetrofitClient.api.obtenerPersonal().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    listaPersonas = response.body() ?: emptyList()
                }
            }
            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {}
        })
    }

    private fun iniciarEscaneo() {
        btnStartScan.isEnabled = false
        tvStatus.text = "Escaneando rostro..."
        pbScanning.visibility = View.VISIBLE
        viewScanLine.visibility = View.VISIBLE
        ivCameraPreview.alpha = 1.0f

        // Animación de línea de escaneo
        val animation = TranslateAnimation(
            0f, 0f, 
            0f, 700f // Ajustar según el tamaño del contenedor
        )
        animation.duration = 2000
        animation.repeatCount = Animation.INFINITE
        viewScanLine.startAnimation(animation)

        // Simular procesamiento
        Handler(Looper.getMainLooper()).postDelayed({
            finalizarEscaneo()
        }, 3000)
    }

    private fun finalizarEscaneo() {
        viewScanLine.clearAnimation()
        viewScanLine.visibility = View.GONE
        pbScanning.visibility = View.GONE
        btnStartScan.isEnabled = true

        if (listaPersonas.isNotEmpty()) {
            // Simulación: 80% de probabilidad de éxito
            val exito = (1..10).random() <= 8
            if (exito) {
                val persona = listaPersonas.random()
                registrarAccesoExitoso(persona)
            } else {
                mostrarResultadoDenegado()
            }
        } else {
            Toast.makeText(context, "No hay personal registrado para comparar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarAccesoExitoso(persona: Persona) {
        val request = AccesoRequest(
            dni = persona.dni,
            tipo = "Entrada",
            dispositivo = "Dispositivo IoT - Reconocimiento Facial"
        )

        RetrofitClient.api.registrarAcceso(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    mostrarResultadoPermitido(persona)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Error al guardar registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarResultadoPermitido(persona: Persona) {
        tvStatus.text = "¡ACCESO PERMITIDO!"
        tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Acceso Concedido")
            .setMessage("Bienvenido(a) ${persona.nombre}\nAcceso registrado correctamente via IoT.")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("Aceptar") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    private fun mostrarResultadoDenegado() {
        tvStatus.text = "¡ACCESO DENEGADO!"
        tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error de Identificación")
            .setMessage("Rostro no reconocido en el sistema de Control de Acceso.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Reintentar") { _, _ ->
                tvStatus.text = "Presione el botón para iniciar escaneo"
                tvStatus.setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}