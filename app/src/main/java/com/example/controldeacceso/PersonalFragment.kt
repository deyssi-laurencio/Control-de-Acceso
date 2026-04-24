package com.example.controldeacceso

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalFragment : Fragment() {

    private lateinit var adapter: PersonaAdapter
    private lateinit var tvNoResults: TextView
    private var listaCompleta: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        tvNoResults = view.findViewById(R.id.tvNoResultsPersonal)
        val etSearchPersonal = view.findViewById<TextInputEditText>(R.id.etSearchPersonal)
        val rvPersonal = view.findViewById<RecyclerView>(R.id.rvPersonal)
        
        // Campos de registro
        val etDni = view.findViewById<TextInputEditText>(R.id.etDniPersonal)
        val etNombre = view.findViewById<TextInputEditText>(R.id.etNombrePersonal)
        val etCargo = view.findViewById<TextInputEditText>(R.id.etCargoPersonal)
        val btnRegistrar = view.findViewById<MaterialButton>(R.id.btnRegistrarPersonal)

        // Configurar RecyclerView
        rvPersonal.layoutManager = LinearLayoutManager(context)
        adapter = PersonaAdapter(emptyList()) { _ -> }
        rvPersonal.adapter = adapter

        // Búsqueda en tiempo real (local sobre la lista cargada de la API)
        etSearchPersonal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Botón Registrar
        btnRegistrar.setOnClickListener {
            val dni = etDni.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val cargo = etCargo.text.toString().trim()

            if (dni.isEmpty() || nombre.isEmpty() || cargo.isEmpty()) {
                Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val nuevaPersona = Persona(0, nombre, dni, cargo)
                RetrofitClient.api.registrarPersonal(nuevaPersona).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Personal registrado con éxito", Toast.LENGTH_SHORT).show()
                            // Limpiar campos
                            etDni.text?.clear()
                            etNombre.text?.clear()
                            etCargo.text?.clear()
                            // Recargar lista
                            cargarPersonal()
                        } else {
                            Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Cargar lista inicial
        cargarPersonal()

        return view
    }

    private fun cargarPersonal() {
        RetrofitClient.api.obtenerPersonal().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    listaCompleta = response.body() ?: emptyList()
                    actualizarUI(listaCompleta)
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(context, "No se pudo cargar la lista", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrar(texto: String) {
        val filtrados = if (texto.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter { 
                it.nombre.contains(texto, ignoreCase = true) || 
                it.dni.contains(texto) 
            }
        }
        actualizarUI(filtrados)
    }

    private fun actualizarUI(lista: List<Persona>) {
        adapter.updateList(lista)
        tvNoResults.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }
}