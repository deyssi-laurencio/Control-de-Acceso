package com.example.controldeacceso

data class UsuarioResponse(
    val nombre: String,
    val correo: String,
    val password: String,
    val foto: String
)

data class UsuarioRequest(
    val nombre: String,
    val password: String,
    val foto: String
)

data class GenericResponse(
    val mensaje: String
)