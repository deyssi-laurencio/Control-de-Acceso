package com.example.controldeacceso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: HistorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)

        dbHelper = DatabaseHelper(requireContext())
        val rvHistorial = view.findViewById<RecyclerView>(R.id.rvHistorial)

        rvHistorial.layoutManager = LinearLayoutManager(context)
        adapter = HistorialAdapter(emptyList())
        rvHistorial.adapter = adapter

        cargarDatos()

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarDatos() // Actualiza los datos cada vez que el fragment se vuelve visible
    }

    private fun cargarDatos() {
        val lista = dbHelper.obtenerHistorial()
        adapter.updateList(lista)
    }
}