package com.example.ratcher.util

import com.example.ratcher.model.Usuario

<<<<<<< HEAD
// Guarda quién inició sesión (y su token de Xano) mientras la app está abierta
object Sesion {
    var usuario: Usuario? = null
    var token: String? = null
=======
// Guarda quién inició sesión mientras la app está abierta
object Sesion {
    var usuario: Usuario? = null
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

    fun nombre(): String {
        return usuario?.nombre ?: "Sin usuario"
    }

    fun cerrar() {
        usuario = null
<<<<<<< HEAD
        token = null
=======
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
    }
}
