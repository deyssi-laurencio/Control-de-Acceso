package com.example.controldeacceso

data class AccesoResponse(
    val id: Int,
    val dni: String,
    val tipo: String,
    val fecha: String,
    val dispositivo: String
)