package com.example.controldeacceso

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BusquedaFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PersonaAdapter
    private lateinit var tvNoResults: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_busqueda, container, false)

        dbHelper = DatabaseHelper(requireContext())
        tvNoResults = view.findViewById(R.id.tvNoResults)
        val etSearchGeneral = view.findViewById<EditText>(R.id.etSearchGeneral)
        val rvBusquedaPersonal = view.findViewById<RecyclerView>(R.id.rvBusquedaPersonal)

        rvBusquedaPersonal.layoutManager = LinearLayoutManager(context)
        // Reutilizamos PersonaAdapter. Pasamos un lambda vacío porque aquí solo es búsqueda.
        adapter = PersonaAdapter(emptyList()) { _ -> }
        rvBusquedaPersonal.adapter = adapter

        etSearchGeneral.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Cargar lista completa al inicio
        buscar("")

        return view
    }

    private fun buscar(filtro: String) {
        val resultados = dbHelper.buscarPersonas(filtro)
        adapter.updateList(resultados)
        
        if (resultados.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
        } else {
            tvNoResults.visibility = View.GONE
        }
    }
}