package com.example.controldeacceso

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusquedaFragment : Fragment() {

    private lateinit var adapter: PersonaAdapter
    private lateinit var tvNoResults: TextView
    private var listaPersonal: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_busqueda, container, false)

        tvNoResults = view.findViewById(R.id.tvNoResults)
        val etSearchGeneral = view.findViewById<EditText>(R.id.etSearchGeneral)
        val rvBusquedaPersonal = view.findViewById<RecyclerView>(R.id.rvBusquedaPersonal)

        rvBusquedaPersonal.layoutManager = LinearLayoutManager(context)
        adapter = PersonaAdapter(emptyList()) { _ -> }
        rvBusquedaPersonal.adapter = adapter

        etSearchGeneral.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        cargarDatosDesdeAPI()

        return view
    }

    private fun cargarDatosDesdeAPI() {
        RetrofitClient.api.obtenerPersonal().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    listaPersonal = response.body() ?: emptyList()
                    adapter.updateList(listaPersonal)
                    actualizarMensajeVacio(listaPersonal.isEmpty())
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrar(texto: String) {
        val filtrados = if (texto.isEmpty()) {
            listaPersonal
        } else {
            listaPersonal.filter { 
                it.nombre.contains(texto, ignoreCase = true) || it.dni.contains(texto) 
            }
        }
        adapter.updateList(filtrados)
        actualizarMensajeVacio(filtrados.isEmpty())
    }

    private fun actualizarMensajeVacio(isEmpty: Boolean) {
        tvNoResults.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}