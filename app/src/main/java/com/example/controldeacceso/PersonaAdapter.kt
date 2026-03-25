package com.example.controldeacceso

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PersonaAdapter(
    private var personas: List<Persona>,
    private val onPersonaSelected: (Persona) -> Unit
) : RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    private var selectedPosition = -1

    class PersonaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvCargo: TextView = view.findViewById(R.id.tvCargo)
        val rbSelected: RadioButton = view.findViewById(R.id.rbSelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_persona, parent, false)
        return PersonaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val persona = personas[position]
        holder.tvNombre.text = persona.nombre
        holder.tvDni.text = "DNI: ${persona.dni}"
        holder.tvCargo.text = persona.cargo
        
        holder.rbSelected.isChecked = position == selectedPosition

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onPersonaSelected(persona)
        }
    }

    override fun getItemCount() = personas.size

    fun updateList(newList: List<Persona>) {
        personas = newList
        selectedPosition = -1
        notifyDataSetChanged()
    }
}