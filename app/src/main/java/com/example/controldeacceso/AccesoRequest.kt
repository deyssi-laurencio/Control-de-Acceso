package com.example.controldeacceso

data class AccesoRequest(
    val dni: String,
    val tipo: String,
    val dispositivo: String = "Android Device"
)