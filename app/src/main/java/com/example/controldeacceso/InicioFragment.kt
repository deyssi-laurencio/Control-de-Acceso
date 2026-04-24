package com.example.controldeacceso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InicioFragment : Fragment() {

    private lateinit var tvIngresos: TextView
    private lateinit var tvSalidas: TextView
    private lateinit var tvAlertas: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Inicializar TextViews del Dashboard
        tvIngresos = view.findViewById(R.id.tvContadorIngresos)
        tvSalidas = view.findViewById(R.id.tvContadorSalidas)
        tvAlertas = view.findViewById(R.id.tvContadorAlertas)

        // Botón Registrar Acceso
        view.findViewById<MaterialCardView>(R.id.btnRegistrarAcceso).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistrarAccesoFragment())
                .addToBackStack(null)
                .commit()
        }

        // Botón Historial
        view.findViewById<MaterialCardView>(R.id.btnHistorial).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistorialFragment())
                .addToBackStack(null)
                .commit()
            
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_historial
        }

        // Botón Personal
        view.findViewById<MaterialCardView>(R.id.btnPersonal).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PersonalFragment())
                .addToBackStack(null)
                .commit()
        }

        // Botón Dispositivo IoT (Reconocimiento Facial)
        view.findViewById<MaterialCardView>(R.id.btnIoT).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FaceRecognitionFragment())
                .addToBackStack(null)
                .commit()
        }

        // Cargar estadísticas desde la API
        actualizarDashboard()

        return view
    }

    override fun onResume() {
        super.onResume()
        actualizarDashboard()
    }

    private fun actualizarDashboard() {
        RetrofitClient.api.obtenerHistorial().enqueue(object : Callback<List<AccesoResponse>> {
            override fun onResponse(
                call: Call<List<AccesoResponse>>,
                response: Response<List<AccesoResponse>>
            ) {
                if (response.isSuccessful) {
                    val listaAccesos = response.body() ?: emptyList()
                    
                    // Obtener fecha de hoy en formato yyyy-MM-dd para filtrar
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val hoy = sdf.format(Date())

                    // Contar ingresos y salidas de HOY
                    val ingresosHoy = listaAccesos.count { 
                        it.tipo.equals("Entrada", ignoreCase = true) && it.fecha.startsWith(hoy)
                    }
                    val salidasHoy = listaAccesos.count { 
                        it.tipo.equals("Salida", ignoreCase = true) && it.fecha.startsWith(hoy)
                    }

                    tvIngresos.text = ingresosHoy.toString()
                    tvSalidas.text = salidasHoy.toString()
                    tvAlertas.text = "0" // Por ahora alertas en 0 o según lógica
                }
            }

            override fun onFailure(call: Call<List<AccesoResponse>>, t: Throwable) {
                // Toast.makeText(context, "Error al actualizar dashboard", Toast.LENGTH_SHORT).show()
            }
        })
    }
}