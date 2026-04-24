package com.example.controldeacceso

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarAccesoFragment : Fragment() {

    private lateinit var adapter: PersonaAdapter
    private var personaSeleccionada: Persona? = null
    private var listaPersonasReal: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_acceso, container, false)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val rvPersonas = view.findViewById<RecyclerView>(R.id.rvPersonas)
        val rgTipoAcceso = view.findViewById<RadioGroup>(R.id.rgTipoAcceso)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)
        val btnScanQR = view.findViewById<Button>(R.id.btnScanQR)
        val btnRegistrarVisitante = view.findViewById<Button>(R.id.btnRegistrarVisitante)

        // Configurar RecyclerView
        rvPersonas.layoutManager = LinearLayoutManager(context)
        adapter = PersonaAdapter(emptyList()) { persona ->
            personaSeleccionada = persona
            Toast.makeText(context, "Seleccionado: ${persona.nombre}", Toast.LENGTH_SHORT).show()
        }
        rvPersonas.adapter = adapter

        // Cargar datos desde la API al iniciar
        cargarPersonasDesdeAPI()

        // Búsqueda en tiempo real sobre la lista de la API
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarLocal(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnScanQR.setOnClickListener {
            mostrarDialogoSimularQR()
        }

        btnRegistrarVisitante.setOnClickListener {
            mostrarDialogoVisitante()
        }

        btnRegistrar.setOnClickListener {
            registrarAccesoActual(rgTipoAcceso)
        }

        return view
    }

    private fun cargarPersonasDesdeAPI() {
        RetrofitClient.api.obtenerPersonal().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    listaPersonasReal = response.body() ?: emptyList()
                    adapter.updateList(listaPersonasReal)
                } else {
                    Toast.makeText(context, "Error al obtener personal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buscarLocal(filtro: String) {
        val resultados = if (filtro.isEmpty()) {
            listaPersonasReal
        } else {
            listaPersonasReal.filter {
                it.nombre.contains(filtro, ignoreCase = true) || it.dni.contains(filtro)
            }
        }
        adapter.updateList(resultados)
    }

    private fun registrarAccesoActual(rgTipoAcceso: RadioGroup) {
        val persona = personaSeleccionada
        if (persona == null) {
            Toast.makeText(context, "Por favor seleccione una persona", Toast.LENGTH_SHORT).show()
            return
        }

        val tipo = if (rgTipoAcceso.checkedRadioButtonId == R.id.rbEntrada) "Entrada" else "Salida"
        
        val request = AccesoRequest(dni = persona.dni, tipo = tipo)
        
        RetrofitClient.api.registrarAcceso(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Acceso Registrado")
                        .setMessage("Se registró la $tipo de ${persona.nombre} correctamente en el servidor.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            parentFragmentManager.popBackStack()
                        }
                        .show()
                } else {
                    Toast.makeText(context, "Error en el servidor al registrar acceso", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoSimularQR() {
        val etDniSimulado = EditText(context)
        etDniSimulado.hint = "Ingrese DNI para simular QR"
        etDniSimulado.setPadding(50, 40, 50, 40)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Simular Escaneo QR")
            .setMessage("Escaneando...")
            .setView(etDniSimulado)
            .setPositiveButton("Escanear") { _, _ ->
                val dni = etDniSimulado.text.toString()
                val personaEncontrada = listaPersonasReal.find { it.dni == dni }
                
                if (personaEncontrada != null) {
                    personaSeleccionada = personaEncontrada
                    Toast.makeText(context, "Persona encontrada: ${personaSeleccionada?.nombre}", Toast.LENGTH_SHORT).show()
                    adapter.updateList(listOf(personaEncontrada))
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("No encontrado")
                        .setMessage("El DNI $dni no está en la base de datos de personal. ¿Desea registrarlo como visitante?")
                        .setPositiveButton("Sí") { _, _ -> mostrarDialogoVisitante(dni) }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoVisitante(dniInicial: String = "") {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_visitante, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etVisitanteNombre)
        val etDni = dialogView.findViewById<TextInputEditText>(R.id.etVisitanteDni)
        val etMotivo = dialogView.findViewById<TextInputEditText>(R.id.etVisitanteMotivo)

        etDni.setText(dniInicial)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Registrar Visitante")
            .setView(dialogView)
            .setPositiveButton("Registrar e Ingresar") { _, _ ->
                val nombre = etNombre.text.toString()
                val dni = etDni.text.toString()
                val motivo = etMotivo.text.toString()

                if (nombre.isNotEmpty() && dni.isNotEmpty()) {
                    // 1. Registrar a la persona como visitante en la API
                    val nuevaPersona = Persona(0, nombre, dni, "Visitante: $motivo")
                    RetrofitClient.api.registrarPersonal(nuevaPersona).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                // 2. Registrar el acceso inmediatamente
                                registrarAccesoAPI(dni, "Entrada")
                            } else {
                                Toast.makeText(context, "Error al registrar visitante", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(context, "Error de red al registrar visitante", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "Nombre y DNI son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarAccesoAPI(dni: String, tipo: String) {
        val request = AccesoRequest(dni, tipo)
        RetrofitClient.api.registrarAcceso(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Visitante registrado y acceso concedido", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Error al registrar acceso de visitante", Toast.LENGTH_SHORT).show()
            }
        })
    }
}