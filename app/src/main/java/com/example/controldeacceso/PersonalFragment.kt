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

class PersonalFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PersonaAdapter
    private lateinit var tvNoResults: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        dbHelper = DatabaseHelper(requireContext())
        tvNoResults = view.findViewById(R.id.tvNoResultsPersonal)
        val etSearchPersonal = view.findViewById<EditText>(R.id.etSearchPersonal)
        val rvPersonal = view.findViewById<RecyclerView>(R.id.rvPersonal)

        // Configurar RecyclerView
        rvPersonal.layoutManager = LinearLayoutManager(context)
        // Reutilizamos el PersonaAdapter existente
        adapter = PersonaAdapter(emptyList()) { _ ->
            // En esta pantalla solo visualizamos, no necesitamos acción al hacer clic
        }
        rvPersonal.adapter = adapter

        // Búsqueda en tiempo real
        etSearchPersonal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Cargar lista completa al abrir el fragment
        filtrar("")

        return view
    }

    private fun filtrar(texto: String) {
        val listaPersonas = dbHelper.buscarPersonas(texto)
        adapter.updateList(listaPersonas)

        if (listaPersonas.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
        } else {
            tvNoResults.visibility = View.GONE
        }
    }
}