package com.example.ratcher.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints de autenticación de Xano
interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body datos: LoginDto): Response<TokenDto>

    @POST("auth/signup")
    suspend fun registrar(@Body datos: RegistroDto): Response<TokenDto>

    // Devuelve el usuario dueño del token enviado en la cabecera Authorization
    @GET("auth/me")
    suspend fun perfil(): Response<UsuarioDto>
}
