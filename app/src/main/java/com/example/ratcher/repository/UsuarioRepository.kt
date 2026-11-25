package com.example.ratcher.repository

import com.example.ratcher.data.ApiClient
import com.example.ratcher.data.AuthApi
import com.example.ratcher.data.LoginDto
import com.example.ratcher.data.RegistroDto
import com.example.ratcher.data.aUsuario
import com.example.ratcher.model.Usuario
import com.example.ratcher.util.Sesion

// Error del servidor con un mensaje claro para mostrar en pantalla
class ErrorServidor(mensaje: String) : Exception(mensaje)

// Repositorio que habla con los endpoints de autenticación de Xano
class UsuarioRepository(private val api: AuthApi = ApiClient.auth) {

    // Inicia sesión: Xano entrega un token y con él se piden los datos del usuario.
    // Devuelve el usuario, o nulo si las credenciales no son correctas.
    suspend fun login(correo: String, clave: String): Usuario? {
        val respuestaLogin = api.login(LoginDto(correo.trim().lowercase(), clave))

        // 400/401/403: Xano rechazó las credenciales
        if (respuestaLogin.code() in listOf(400, 401, 403)) return null
        if (!respuestaLogin.isSuccessful) {
            throw ErrorServidor("Error del servidor al iniciar sesión (código ${respuestaLogin.code()})")
        }

        val token = respuestaLogin.body()?.authToken
        if (token.isNullOrBlank()) {
            throw ErrorServidor("El servidor no devolvió el token de sesión. Revisa el endpoint auth/login en Xano")
        }

        Sesion.token = token
        val respuestaPerfil = api.perfil()
        val perfil = respuestaPerfil.body()
        if (!respuestaPerfil.isSuccessful || perfil == null) {
            Sesion.token = null
            throw ErrorServidor("No se pudo obtener el perfil del usuario. Revisa el endpoint auth/me en Xano")
        }
        return perfil.aUsuario()
    }

    // Crea la cuenta en Xano (la contraseña la guarda Xano con hash).
    // Devuelve false si el correo ya existe o los datos fueron rechazados.
    suspend fun registrar(nombre: String, correo: String, cargo: String, clave: String): Boolean {
        val datos = RegistroDto(
            name = nombre.trim(),
            email = correo.trim().lowercase(),
            password = clave,
            cargo = cargo
        )
        val respuesta = api.registrar(datos)
        if (respuesta.code() in listOf(400, 403, 409)) return false
        if (!respuesta.isSuccessful) {
            throw ErrorServidor("Error del servidor al registrar (código ${respuesta.code()})")
        }
        // Si Xano responde 200 pero sin token, el endpoint no guardó nada
        if (respuesta.body()?.authToken.isNullOrBlank()) {
            throw ErrorServidor("El servidor no creó la cuenta. Revisa el endpoint auth/signup en Xano")
        }
        return true
    }
}
