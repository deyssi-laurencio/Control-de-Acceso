package com.example.controldeacceso

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistorialAdapter(
    private var accesos: List<AccesoResponse>,
    private var listaPersonal: List<Persona> = emptyList()
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreHist)
        val tvDni: TextView = view.findViewById(R.id.tvUbicacionHist)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoHist)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaHist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val acceso = accesos[position]
        
        // Buscar el nombre de la persona por su DNI en la lista de personal
        val persona = listaPersonal.find { it.dni == acceso.dni }
        
        holder.tvNombre.text = persona?.nombre ?: "Visitante / Desconocido"
        holder.tvDni.text = "DNI: ${acceso.dni}"
        holder.tvTipo.text = acceso.tipo.uppercase()
        
        // Formatear fecha y hora
        holder.tvFecha.text = formatFechaHora(acceso.fecha)

        if (acceso.tipo.contains("Entrada", ignoreCase = true)) {
            holder.tvTipo.setTextColor(Color.parseColor("#2E7D32")) // Verde
        } else {
            holder.tvTipo.setTextColor(Color.parseColor("#C62828")) // Rojo
        }
    }

    private fun formatFechaHora(fechaIso: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(fechaIso)
            val formatter = SimpleDateFormat("dd/MM/yyyy  -  HH:mm:ss", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            fechaIso
        }
    }

    override fun getItemCount() = accesos.size

    fun updateData(newAccesos: List<AccesoResponse>, newPersonal: List<Persona>) {
        this.accesos = newAccesos
        this.listaPersonal = newPersonal
        notifyDataSetChanged()
    }
}