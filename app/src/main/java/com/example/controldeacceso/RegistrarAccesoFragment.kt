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
import com.google.android.material.textfield.TextInputLayout

class RegistrarAccesoFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PersonaAdapter
    private var personaSeleccionada: Persona? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_acceso, container, false)

        dbHelper = DatabaseHelper(requireContext())
        
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
        }
        rvPersonas.adapter = adapter

        // Búsqueda en tiempo real
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Simular Escaneo QR
        btnScanQR.setOnClickListener {
            mostrarDialogoSimularQR()
        }

        // Registrar Visitante
        btnRegistrarVisitante.setOnClickListener {
            mostrarDialogoVisitante()
        }

        btnRegistrar.setOnClickListener {
            registrarAccesoActual(rgTipoAcceso)
        }

        return view
    }

    private fun buscar(filtro: String) {
        val resultados = dbHelper.buscarPersonas(filtro)
        adapter.updateList(resultados)
    }

    private fun registrarAccesoActual(rgTipoAcceso: RadioGroup) {
        val persona = personaSeleccionada
        if (persona == null) {
            Toast.makeText(context, "Por favor seleccione una persona", Toast.LENGTH_SHORT).show()
            return
        }

        val tipo = if (rgTipoAcceso.checkedRadioButtonId == R.id.rbEntrada) "Entrada" else "Salida"
        
        val exito = dbHelper.registrarAcceso(persona.id, tipo)
        if (exito) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Acceso Registrado")
                .setMessage("Se registró la $tipo de ${persona.nombre}")
                .setPositiveButton("Aceptar") { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .show()
        } else {
            Toast.makeText(context, "Error al registrar acceso", Toast.LENGTH_SHORT).show()
        }
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
                val personas = dbHelper.buscarPersonas(dni)
                if (personas.isNotEmpty()) {
                    personaSeleccionada = personas[0]
                    Toast.makeText(context, "Persona encontrada: ${personaSeleccionada?.nombre}", Toast.LENGTH_SHORT).show()
                    adapter.updateList(personas)
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("No encontrado")
                        .setMessage("El DNI $dni no está registrado. ¿Desea registrarlo como visitante?")
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

                if (nombre.isNotEmpty()) {
                    // Guardar como persona primero
                    val db = dbHelper.writableDatabase
                    val values = android.content.ContentValues().apply {
                        put("nombre", nombre)
                        put("dni", dni)
                        put("cargo", "Visitante: $motivo")
                    }
                    val id = db.insert("personas", null, values)
                    if (id != -1L) {
                        dbHelper.registrarAcceso(id.toInt(), "Entrada")
                        Toast.makeText(context, "Visitante registrado y acceso concedido", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                } else {
                    Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}