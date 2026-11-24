package com.example.ratcher.util

import com.example.ratcher.model.Usuario

// Guarda quién inició sesión mientras la app está abierta
object Sesion {
    var usuario: Usuario? = null

    fun nombre(): String {
        return usuario?.nombre ?: "Sin usuario"
    }

    fun cerrar() {
        usuario = null
    }
}
