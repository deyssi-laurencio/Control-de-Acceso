package com.example.controldeacceso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class InicioFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvIngresos: TextView
    private lateinit var tvSalidas: TextView
    private lateinit var tvAlertas: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        dbHelper = DatabaseHelper(requireContext())

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

        // Botón Personal - Conectado
        view.findViewById<MaterialCardView>(R.id.btnPersonal).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PersonalFragment())
                .addToBackStack(null)
                .commit()
        }

        // Cargar estadísticas iniciales
        actualizarDashboard()

        return view
    }

    override fun onResume() {
        super.onResume()
        actualizarDashboard() // Asegura que los números se actualicen al volver a esta pantalla
    }

    private fun actualizarDashboard() {
        val ingresosHoy = dbHelper.contarAccesosHoy("Entrada")
        val salidasHoy = dbHelper.contarAccesosHoy("Salida")
        val alertasHoy = dbHelper.contarAlertasHoy()

        tvIngresos.text = ingresosHoy.toString()
        tvSalidas.text = salidasHoy.toString()
        tvAlertas.text = alertasHoy.toString()
    }
}