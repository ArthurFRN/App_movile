package com.example.ratcher.repository

import android.content.Context
import com.example.ratcher.model.Usuario
import com.example.ratcher.remote.RetrofitInstance
import com.example.ratcher.util.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.example.ratcher.remote.ApiService.UsuarioDto

class UsuarioRepository(context: Context) {

    private val api = RetrofitInstance.api

    suspend fun login(correo: String, clave: String): Usuario? = withContext(Dispatchers.IO) {
        api.getUsuarios(correo.trim().lowercase(), Seguridad.hash(clave))
            .firstOrNull()
            ?.let { Usuario(id = it.id, correo = it.correo, nombre = it.nombre, cargo = it.cargo) }
    }

    suspend fun registrar(nombre: String, correo: String, cargo: String, clave: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                api.createUsuario(
                    UsuarioDto(
                        nombre = nombre.trim(),
                        correo = correo.trim().lowercase(),
                        cargo = cargo,
                        clave = Seguridad.hash(clave)
                    )
                )
                true
            } catch (e: HttpException) {
                false // correo repetido (índice único)
            }
        }
}