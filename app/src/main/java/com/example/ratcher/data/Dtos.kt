package com.example.ratcher.data

import com.google.gson.annotations.SerializedName

// Objetos que viajan por la red. Los nombres JSON son los campos de las tablas de Xano.

data class LoginDto(val email: String, val password: String)

data class RegistroDto(val name: String, val email: String, val password: String, val cargo: String)

// Xano suele devolver "authToken"; se aceptan también otros nombres comunes
data class TokenDto(
    @SerializedName(value = "authToken", alternate = ["auth_token", "token"]) val authToken: String?
)

data class UsuarioDto(val id: Long?, val name: String?, val email: String?, val cargo: String?)

data class LevantamientoDto(
    val id: Long? = null,
    @SerializedName("rack_id") val rackId: String?,
    val area: String?,
    val fecha: String?,
    val observaciones: String?,
    @SerializedName("ruta_imagen") val rutaImagen: String?,
    val responsable: String?,
    val estado: String?
)

data class EstadoDto(val estado: String)

data class ComponenteDto(
    val id: Long? = null,
    @SerializedName("levantamiento_id") val levantamientoId: Long?,
    val categoria: String?,
    val descripcion: String?,
    val confianza: Int?,
    val estado: String?,
    @SerializedName("categoria_original") val categoriaOriginal: String?
)

// Datos que se pueden cambiar al revisar un componente (confirmar, corregir o rechazar)
data class ComponenteEdicionDto(val categoria: String, val descripcion: String, val estado: String)

data class HistorialDto(
    val id: Long? = null,
    @SerializedName("levantamiento_id") val levantamientoId: Long?,
    val accion: String?,
    val detalle: String?,
    val fecha: String?,
    val usuario: String?
)
