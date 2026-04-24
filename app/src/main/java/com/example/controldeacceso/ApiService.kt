package com.example.controldeacceso

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("registro")
    fun registrar(@Body request: LoginRequest): Call<LoginResponse>

    @GET("personal")
    fun obtenerPersonal(): Call<List<Persona>>

    @POST("personal")
    fun registrarPersonal(@Body request: Persona): Call<LoginResponse>

    @POST("acceso")
    fun registrarAcceso(@Body request: AccesoRequest): Call<LoginResponse>

    @GET("acceso")
    fun obtenerHistorial(): Call<List<AccesoResponse>>

    @GET("usuario/{correo}")
    fun obtenerUsuario(@Path("correo") correo: String): Call<UsuarioResponse>

    @PUT("usuario/{correo}")
    fun actualizarUsuario(
        @Path("correo") correo: String,
        @Body request: UsuarioRequest
    ): Call<GenericResponse>
}