package com.example.controldeacceso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialFragment : Fragment() {

    private lateinit var adapter: HistorialAdapter
    private var listaPersonalReal: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)

        val rvHistorial = view.findViewById<RecyclerView>(R.id.rvHistorial)

        rvHistorial.layoutManager = LinearLayoutManager(context)
        adapter = HistorialAdapter(emptyList())
        rvHistorial.adapter = adapter

        cargarTodo()

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarTodo()
    }

    private fun cargarTodo() {
        // Primero cargamos el personal para tener los nombres
        RetrofitClient.api.obtenerPersonal().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    listaPersonalReal = response.body() ?: emptyList()
                    // Una vez que tenemos los nombres, cargamos el historial
                    cargarHistorial()
                }
            }
            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar personal", Toast.LENGTH_SHORT).show()
                cargarHistorial() // Intentamos cargar el historial de todos modos
            }
        })
    }

    private fun cargarHistorial() {
        RetrofitClient.api.obtenerHistorial().enqueue(object : Callback<List<AccesoResponse>> {
            override fun onResponse(call: Call<List<AccesoResponse>>, response: Response<List<AccesoResponse>>) {
                if (response.isSuccessful) {
                    val listaAccesos = response.body() ?: emptyList()
                    // Enviamos tanto los accesos como la lista de personal al adaptador
                    adapter.updateData(listaAccesos, listaPersonalReal)
                }
            }

            override fun onFailure(call: Call<List<AccesoResponse>>, t: Throwable) {
                Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}