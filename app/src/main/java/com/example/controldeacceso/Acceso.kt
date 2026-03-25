package com.example.controldeacceso

data class Acceso(
    val id: Int,
    val nombrePersona: String,
    val dniPersona: String,
    val cargoPersona: String,
    val tipo: String,
    val fecha: String
)