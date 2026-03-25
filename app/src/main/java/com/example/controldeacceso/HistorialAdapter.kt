package com.example.controldeacceso

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private var accesos: List<Acceso>) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreHist)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacionHist)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoHist)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaHist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val acceso = accesos[position]
        holder.tvNombre.text = acceso.nombrePersona
        holder.tvUbicacion.text = "Ubicación: ${acceso.cargoPersona}"
        holder.tvTipo.text = acceso.tipo
        holder.tvFecha.text = acceso.fecha

        if (acceso.tipo == "Entrada") {
            holder.tvTipo.setTextColor(Color.parseColor("#2E7D32")) // Verde
        } else {
            holder.tvTipo.setTextColor(Color.parseColor("#C62828")) // Rojo
        }
    }

    override fun getItemCount() = accesos.size

    fun updateList(newList: List<Acceso>) {
        accesos = newList
        notifyDataSetChanged()
    }
}